/*
 *  Env.scala
 *  (ScalaCollider)
 *
 *  Copyright (c) 2008-2013 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either
 *  version 2, june 1991 of the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License (gpl.txt) along with this software; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package de.sciss.synth
package ugen

import collection.immutable.{IndexedSeq => IIdxSeq, Seq => ISeq}
import de.sciss.synth.Curve.{sine => sin, _}
import language.implicitConversions

sealed trait EnvFactory[V] {
  import Env.{Segment => Seg}

  protected def create(startLevel: GE, segments: IIdxSeq[Seg]): V

  // fixed duration envelopes
  def triangle: V = triangle()

  def triangle(dur: GE = 1, level: GE = 1): V = {
    val durH = dur * 0.5f
    create(0, Vector[Seg](durH -> level, durH -> 0))
  }

  def sine: V = sine()

  def sine(dur: GE = 1, level: GE = 1): V = {
    val durH = dur * 0.5f
    create(0, Vector[Seg]((durH, level, sin), (durH, 0, sin)))
  }

  def perc: V = perc()

  def perc(attack: GE = 0.01, release: GE = 1, level: GE = 1,
           shape: Env.Curve = parametric(-4)): V =
    create(0, Vector[Seg]((attack, level, shape), (release, 0, shape)))

  def linen: V = linen()

  def linen(attack: GE = 0.01f, sustain: GE = 1, release: GE = 1,
            level: GE = 1, shape: Env.Curve = linear): V =
    create(0, Vector[Seg]((attack, level, shape), (sustain, level, shape), (release, 0, shape)))
}

object Env extends EnvFactory[Env] {
  object Curve {
    implicit def const(peer: de.sciss.synth.Curve): Curve = Const(peer)

    final case class Const(peer: de.sciss.synth.Curve) extends Curve {
      def id       : GE = Constant(peer.id)
      def curvature: GE = peer match {
        case parametric(c)  => Constant(c)
        case _              => Constant(0)
      }
    }

    def apply(id: GE, curvature: GE = 0): Curve = new Apply(id, curvature)
    def unapply(s: Curve): Option[(GE, GE)] = Some(s.id, s.curvature)

    private final case class Apply(id: GE, curvature: GE) extends Curve {
      override def productPrefix = "Env$Curve"
    }
  }
  sealed trait Curve {
    def id: GE
    def curvature: GE
  }

  object Segment {
    implicit def fromTuple3[D, L, S](tup: (D, L, S))
                                    (implicit durView: D => GE, levelView: L => GE, slopeView: S => Curve): Segment =
      Segment(tup._1, tup._2, tup._3)

    implicit def fromTuple2[D, L](tup: (D, L))(implicit durView: D => GE, levelView: L => GE): Segment =
      Segment(tup._1, tup._2, linear)
  }
  final case class Segment(dur: GE, targetLevel: GE, slope: Curve = linear)

  protected def create(startLevel: GE, segments: IIdxSeq[Segment]) = new Env(startLevel, segments)

  // envelopes with sustain
  def cutoff(release: GE = 0.1f, level: GE = 1, slope: Curve = linear): Env = {
    val releaseLevel: GE = slope match {
      case Curve.Const(`exponential`) => 1e-05f // dbamp( -100 )
      case _ => 0
    }
    new Env(level, (release, releaseLevel, slope) :: Nil, 0)
  }

//  def dadsr(delay: GE = 0.1f, attack: GE = 0.01f, decay: GE = 0.3f, sustainLevel: GE = 0.5f, release: GE = 1,
//            peakLevel: GE = 1, shape: Curve = parametric(-4), bias: GE = 0): Env =
//    new Env(bias, List(Seg(delay, bias, shape),
//      Seg(attack, peakLevel + bias, shape),
//      Seg(decay, peakLevel * sustainLevel + bias, shape),
//      Seg(release, bias, shape)), 3)
//
//  def adsr(attack: GE = 0.01f, decay: GE = 0.3f, sustainLevel: GE = 0.5f, release: GE = 1, peakLevel: GE = 1,
//           shape: Curve = parametric(-4), bias: GE = 0): Env =
//    new Env(bias, List(Seg(attack, bias, shape),
//      Seg(decay, peakLevel * sustainLevel + bias, shape),
//      Seg(release, bias, shape)), 2)
//
//  def asr(attack: GE = 0.01f, level: GE = 1, release: GE = 1, shape: Curve = parametric(-4)): Env =
//    new Env(0, List(Seg(attack, level, shape), Seg(release, 0, shape)), 1)
}

sealed trait EnvLike extends GE {
  def startLevel: GE
  def segments: Seq[Env.Segment]
  def isSustained: Boolean
}

final case class Env(startLevel: GE, segments: ISeq[Env.Segment],
                     releaseNode: GE = -99, loopNode: GE = -99)
  extends EnvLike {

  private[synth] def expand: UGenInLike = toGE

  private def toGE: GE = {
    val segmIdx = segments.toIndexedSeq
    val sizeGE: GE = segmIdx.size
    val res: IIdxSeq[GE] = startLevel +: sizeGE +: releaseNode +: loopNode +: segmIdx.flatMap(seg =>
      Vector[GE](seg.targetLevel, seg.dur, seg.slope.id, seg.slope.curvature))
    res
  }

  def rate: MaybeRate = toGE.rate

  def isSustained = releaseNode != Constant(-99)
}

object IEnv extends EnvFactory[IEnv] {
  protected def create(startLevel: GE, segments: IIdxSeq[Env.Segment]) = new IEnv(startLevel, segments)
}

final case class IEnv(startLevel: GE, segments: ISeq[Env.Segment], offset: GE = 0)
  extends EnvLike {

  private[synth] def expand: UGenInLike = toGE

  private def toGE: GE = {
    val segmIdx     = segments.toIndexedSeq
    val sizeGE: GE  = segmIdx.size
    val totalDur    = segmIdx.foldLeft[GE](0)((sum, next) => sum + next.dur)
    val res: IIdxSeq[GE] = offset +: startLevel +: sizeGE +: totalDur +: segmIdx.flatMap(seg =>
      Vector[GE](seg.dur, seg.slope.id, seg.slope.curvature, seg.targetLevel))
    res
  }

  def rate: MaybeRate = toGE.rate

  def isSustained = false
}