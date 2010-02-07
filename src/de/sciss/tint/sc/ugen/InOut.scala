/*
 *  InOut.scala
 *  Tintantmare
 *
 *  Copyright (c) 2008-2010 Hanns Holger Rutz. All rights reserved.
 *
 *	This software is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 *	as published by the Free Software Foundation; either
 *	version 2, june 1991 of the License, or (at your option) any later version.
 *
 *	This software is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *	General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public
 *	License (gpl.txt) along with this software; if not, write to the Free Software
 *	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *	For further information, please contact Hanns Holger Rutz at
 *	contact@sciss.de
 *
 *
 *  Changelog:
 */

package de.sciss.tint.sc.ugen

import de.sciss.tint.sc._
import SC._
import GraphBuilder._

/**
 *	@version	0.11, 03-Jan-10
 */

object In {
  def ar( bus: GE, numChannels: Int = 1 ) : GE = make( audio, bus, numChannels )
  def kr( bus: GE, numChannels: Int = 1 ) : GE = make( control, bus, numChannels )

  protected def make( rate: Rate, bus: GE, numChannels: Int ) : GE = {
    simplify( for( List( b ) <- expand( bus )) yield this( rate, b, numChannels ))
  }
}
case class In( rate: Rate, bus: UGenIn, numChannels: Int )
extends MultiOutUGen( List.fill[ Rate ]( numChannels )( rate ), List( bus ))

object LocalIn {
  def ar( numChannels: Int = 1 ) : GE = this( audio, numChannels )
  def kr( numChannels: Int = 1 ) : GE = this( control, numChannels )
}
case class LocalIn( rate: Rate, numChannels: Int )
extends MultiOutUGen( List.fill[ Rate ]( numChannels )( rate ), Nil )

object LagIn {
  def kr( bus: GE, numChannels: Int = 1, lag: GE = 0.1f ) : GE = {
    simplify( for( List( b, l ) <- expand( bus, lag ))
      yield this( b, numChannels, l ))
  }
}
case class LagIn( bus: UGenIn, numChannels: Int, lag: UGenIn )
extends MultiOutUGen( List.fill[ Rate ]( numChannels )( audio ), List( bus, lag ))
with ControlRated

object InFeedback {
  def ar( bus: GE, numChannels: Int = 1 ) : GE = {
    simplify( for( List( b ) <- expand( bus )) yield this( b, numChannels ))
  }
}
case class InFeedback( bus: UGenIn, numChannels: Int )
extends MultiOutUGen( List.fill[ Rate ]( numChannels )( audio ), List( bus ))
with AudioRated

object InTrig {
  def kr( bus: GE, numChannels: Int = 1 ) : GE = {
    simplify( for( List( b ) <- expand( bus )) yield this( b, numChannels ))
  }
}
case class InTrig( bus: UGenIn, numChannels: Int )
extends MultiOutUGen( List.fill[ Rate ]( numChannels )( audio ), List( bus ))
with ControlRated

abstract class AbstractOut {
  def ar( bus: GE, multi: GE ) : GE = make( audio, bus, multi )
  def kr( bus: GE, multi: GE ) : GE = make( control, bus, multi )

  private def make( rate: Rate, bus: GE, multi: GE ) : GE = {
    val args = bus :: replaceZeroesWithSilence( multi ).toUGenIns.toList
    simplify( for( b :: m <- expand( args: _* ))
      yield this( rate, b, m ))
  }

  def apply( rate: Rate, bus: UGenIn, multi: Seq[ UGenIn ]) : GE
}

object Out extends AbstractOut
case class Out( rate: Rate, bus: UGenIn, multi: Seq[ UGenIn ])
extends ZeroOutUGen( (bus :: multi.toList): _* )

object ReplaceOut extends AbstractOut
case class ReplaceOut( rate: Rate, bus: UGenIn, multi: Seq[ UGenIn ])
extends ZeroOutUGen( (bus :: multi.toList): _* )

object OffsetOut {
  def ar( bus: GE, multi: GE ) : GE = {
    val args = bus :: replaceZeroesWithSilence( multi ).toUGenIns.toList
    simplify( for( b :: m <- expand( args: _* ))
      yield this( b, m ))
  }
}
case class OffsetOut( bus: UGenIn, multi: Seq[ UGenIn ])
extends ZeroOutUGen( (bus :: multi.toList): _* ) with AudioRated

object LocalOut {
  def ar( multi: GE ) : GE = make( audio, multi )
  def kr( multi: GE ) : GE = make( control, multi )

  private def make( rate: Rate, multi: GE ) : GE = {
    val ins = replaceZeroesWithSilence( multi ).toUGenIns
    this( rate, ins )
  }
}
case class LocalOut( rate: Rate, multi: Seq[ UGenIn ])
extends ZeroOutUGen( multi: _* )

object XOut {
  def ar( bus: GE, xfade: GE, multi: GE ) : GE = make( audio, bus, xfade, multi )
  def kr( bus: GE, xfade: GE, multi: GE ) : GE = make( control, bus, xfade, multi )

  private def make( rate: Rate, bus: GE, xfade: GE, multi: GE ) : GE = {
    val args = bus :: xfade :: replaceZeroesWithSilence( multi ).toUGenIns.toList
    simplify( for( b :: x :: m <- expand( args: _* ))
      yield this( rate, b, x, m ))
  }
}
case class XOut( rate: Rate, bus: UGenIn, xfade: UGenIn, multi: Seq[ UGenIn ])
extends ZeroOutUGen( (bus :: xfade :: multi.toList): _* )