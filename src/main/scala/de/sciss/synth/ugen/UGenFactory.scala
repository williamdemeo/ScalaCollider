/*
 *  UGenFactory.scala
 *  (ScalaCollider)
 *
 *  Copyright (c) 2008-2010 Hanns Holger Rutz. All rights reserved.
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
 *
 *
 *  Changelog:
 */

package de.sciss.synth.ugen

import de.sciss.synth.{ audio, control, GE, Rate, SynthGraph, UGenIn, scalar }
import SynthGraph._  // simplify, individuate

private[ugen] trait UGen1Args {
   def apply( rate: Rate, arg1: UGenIn ) : GE
   private def make( rate: Rate, arg1: GE ) : GE =
      simplify( for( List( a1 ) <- expand( arg1 )) yield this( rate, a1 ))

   protected def arExp( arg1: GE ) : GE = make( audio, arg1 )
   protected def krExp( arg1: GE ) : GE = make( control, arg1 )
   protected def irExp( arg1: GE ) : GE = make( scalar, arg1 )
}

private[ugen] trait UGen1RArgs { // single rate
   def apply( arg1: UGenIn ) : GE
   protected def make( arg1: GE ) : GE =
      simplify( for( List( a1 ) <- expand( arg1 )) yield this( a1 ))
}

private[ugen] trait UGen1ArgsIndiv {
   def apply( rate: Rate, arg1: UGenIn, _indiv: Int ) : GE
   private def make( rate: Rate, arg1: GE ) : GE =
      simplify( for( List( a1 ) <- expand( arg1 )) yield this( rate, a1, individuate ))

   protected def arExp( arg1: GE ) : GE = make( audio, arg1 )
   protected def krExp( arg1: GE ) : GE = make( control, arg1 )
   protected def irExp( arg1: GE ) : GE = make( scalar, arg1 )
}

private[ugen] trait UGen2Args {
   def apply( rate: Rate, arg1: UGenIn, arg2: UGenIn ) : GE
   private def make( rate: Rate, arg1: GE, arg2: GE ) : GE =
      simplify( for( List( a1, a2 ) <- expand( arg1, arg2 ))
         yield this( rate, a1, a2 ))

   protected def arExp( arg1: GE, arg2: GE ) : GE = make( audio, arg1, arg2 )
   protected def krExp( arg1: GE, arg2: GE ) : GE = make( control, arg1, arg2 )
   protected def irExp( arg1: GE, arg2: GE ) : GE = make( scalar, arg1, arg2 )
}

private[ugen] trait UGen2RArgs { // single rate
   def apply( arg1: UGenIn, arg2: UGenIn ) : GE
   protected def make( arg1: GE, arg2: GE ) : GE =
      simplify( for( List( a1, a2 ) <- expand( arg1, arg2 )) yield this( a1, a2 ))
}

private[ugen] trait UGen2ArgsIndiv {
   def apply( rate: Rate, arg1: UGenIn, arg2: UGenIn, _indiv: Int ) : GE
   private def make( rate: Rate, arg1: GE, arg2: GE ) : GE =
      simplify( for( List( a1, a2 ) <- expand( arg1, arg2 ))
         yield this( rate, a1, a2, individuate ))

   protected def arExp( arg1: GE, arg2: GE ) : GE = make( audio, arg1, arg2 )
   protected def krExp( arg1: GE, arg2: GE ) : GE = make( control, arg1, arg2 )
   protected def irExp( arg1: GE, arg2: GE ) : GE = make( scalar, arg1, arg2 )
}

private[ugen] trait UGen2RArgsIndiv {
   def apply( arg1: UGenIn, arg2: UGenIn, _indiv: Int ) : GE
   protected def make( arg1: GE, arg2: GE ) : GE =
      simplify( for( List( a1, a2 ) <- expand( arg1, arg2 )) yield this( a1, a2, individuate ))
}

private[ugen] trait UGen3Args {
   def apply( rate: Rate, arg1: UGenIn, arg2: UGenIn, arg3: UGenIn ) : GE
   private def make( rate: Rate, arg1: GE, arg2: GE, arg3: GE ) : GE =
      simplify( for( List( a1, a2, a3 ) <- expand( arg1, arg2, arg3 ))
         yield this( rate, a1, a2, a3 ))

   protected def arExp( arg1: GE, arg2: GE, arg3: GE ) : GE =
      make( audio, arg1, arg2, arg3 )
   protected def krExp( arg1: GE, arg2: GE, arg3: GE ) : GE =
      make( control, arg1, arg2, arg3 )
   protected def irExp( arg1: GE, arg2: GE, arg3: GE ) : GE =
      make( scalar, arg1, arg2, arg3 )
}

private[ugen] trait UGen3RArgs { // single rate
   def apply( arg1: UGenIn, arg2: UGenIn, arg3: UGenIn ) : GE
   protected def make( arg1: GE, arg2: GE, arg3: GE ) : GE =
      simplify( for( List( a1, a2, a3 ) <- expand( arg1, arg2, arg3 ))
         yield this( a1, a2, a3 ))
}

private[ugen] trait UGen3ArgsIndiv {
   def apply( rate: Rate, arg1: UGenIn, arg2: UGenIn, arg3: UGenIn, _indiv: Int ) : GE
   private def make( rate: Rate, arg1: GE, arg2: GE, arg3: GE ) : GE =
     simplify( for( List( a1, a2, a3 ) <- expand( arg1, arg2, arg3 ))
       yield this( rate, a1, a2, a3, individuate ))

   protected def arExp( arg1: GE, arg2: GE, arg3: GE ) : GE =
      make( audio, arg1, arg2, arg3 )
   protected def krExp( arg1: GE, arg2: GE, arg3: GE ) : GE =
      make( control, arg1, arg2, arg3 )
   protected def irExp( arg1: GE, arg2: GE, arg3: GE ) : GE =
      make( scalar, arg1, arg2, arg3 )
}

private[ugen] trait UGen3RArgsIndiv {
   def apply( arg1: UGenIn, arg2: UGenIn, arg3: UGenIn, _indiv: Int ) : GE
   protected def make( arg1: GE, arg2: GE, arg3: GE ) : GE =
      simplify( for( List( a1, a2, a3 ) <- expand( arg1, arg2, arg3 ))
         yield this( a1, a2, a3, individuate ))
}

private[ugen] trait UGen4Args {
   def apply( rate: Rate, arg1: UGenIn, arg2: UGenIn, arg3: UGenIn, arg4: UGenIn ) : GE
   private def make( rate: Rate, arg1: GE, arg2: GE, arg3: GE, arg4: GE ) : GE =
      simplify( for( List( a1, a2, a3, a4 ) <- expand( arg1, arg2, arg3, arg4 ))
         yield this( rate, a1, a2, a3, a4 ))

   protected def arExp( arg1: GE, arg2: GE, arg3: GE, arg4: GE ) : GE =
      make( audio, arg1, arg2, arg3, arg4 )
   protected def krExp( arg1: GE, arg2: GE, arg3: GE, arg4: GE ) : GE =
      make( control, arg1, arg2, arg3, arg4 )
   protected def irExp( arg1: GE, arg2: GE, arg3: GE, arg4: GE ) : GE =
      make( scalar, arg1, arg2, arg3, arg4 )
}

private[ugen] trait UGen4RArgs {
   def apply( arg1: UGenIn, arg2: UGenIn, arg3: UGenIn, arg4: UGenIn ) : GE
   protected def make( arg1: GE, arg2: GE, arg3: GE, arg4: GE ) : GE =
      simplify( for( List( a1, a2, a3, a4 ) <- expand( arg1, arg2, arg3, arg4 ))
         yield this( a1, a2, a3, a4 ))
}

private[ugen] trait UGen5Args {
   def apply( rate: Rate, arg1: UGenIn, arg2: UGenIn, arg3: UGenIn, arg4: UGenIn, arg5: UGenIn ) : GE
   private def make( rate: Rate, arg1: GE, arg2: GE, arg3: GE, arg4: GE, arg5: GE ) : GE =
      simplify( for( List( a1, a2, a3, a4, a5 ) <- expand( arg1, arg2, arg3, arg4, arg5 ))
         yield this( audio, a1, a2, a3, a4, a5 ))

   protected def arExp( arg1: GE, arg2: GE, arg3: GE, arg4: GE, arg5: GE ) : GE =
      make( audio, arg1, arg2, arg3, arg4, arg5 )
   protected def krExp( arg1: GE, arg2: GE, arg3: GE, arg4: GE, arg5: GE ) : GE =
      make( control, arg1, arg2, arg3, arg4, arg5 )
   protected def irExp( arg1: GE, arg2: GE, arg3: GE, arg4: GE, arg5: GE ) : GE =
      make( scalar, arg1, arg2, arg3, arg4, arg5 )
}

private[ugen] trait UGen5RArgs {
   def apply( arg1: UGenIn, arg2: UGenIn, arg3: UGenIn, arg4: UGenIn, arg5: UGenIn ) : GE
   protected def make( arg1: GE, arg2: GE, arg3: GE, arg4: GE, arg5: GE ) : GE =
      simplify( for( List( a1, a2, a3, a4, a5 ) <- expand( arg1, arg2, arg3, arg4, arg5 ))
         yield this( a1, a2, a3, a4, a5 ))
}

private[ugen] trait UGen6Args {
   def apply( rate: Rate, arg1: UGenIn, arg2: UGenIn, arg3: UGenIn, arg4: UGenIn,
              arg5: UGenIn, arg6: UGenIn ) : GE
   private def make( rate: Rate, arg1: GE, arg2: GE, arg3: GE, arg4: GE,
                     arg5: GE, arg6: GE ) : GE =
      simplify( for( List( a1, a2, a3, a4, a5, a6 ) <- expand( arg1, arg2, arg3, arg4, arg5, arg6 ))
         yield this( audio, a1, a2, a3, a4, a5, a6 ))

   protected def arExp( arg1: GE, arg2: GE, arg3: GE, arg4: GE, arg5: GE, arg6: GE ) : GE =
      make( audio, arg1, arg2, arg3, arg4, arg5, arg6 )
   protected def krExp( arg1: GE, arg2: GE, arg3: GE, arg4: GE, arg5: GE, arg6: GE ) : GE =
      make( control, arg1, arg2, arg3, arg4, arg5, arg6 )
   protected def irExp( arg1: GE, arg2: GE, arg3: GE, arg4: GE, arg5: GE, arg6: GE ) : GE =
      make( scalar, arg1, arg2, arg3, arg4, arg5, arg6 )
}

private[ugen] trait UGen7Args {
   def apply( rate: Rate, arg1: UGenIn, arg2: UGenIn, arg3: UGenIn, arg4: UGenIn,
              arg5: UGenIn, arg6: UGenIn, arg7: UGenIn ) : GE
   private def make( rate: Rate, arg1: GE, arg2: GE, arg3: GE, arg4: GE,
                     arg5: GE, arg6: GE, arg7: GE ) : GE =
      simplify( for( List( a1, a2, a3, a4, a5, a6, a7 ) <-
                  expand( arg1, arg2, arg3, arg4, arg5, arg6, arg7 ))
         yield this( audio, a1, a2, a3, a4, a5, a6, a7 ))

   protected def arExp( arg1: GE, arg2: GE, arg3: GE, arg4: GE, arg5: GE,
                        arg6: GE, arg7: GE ) : GE =
      make( audio, arg1, arg2, arg3, arg4, arg5, arg6, arg7 )
   protected def krExp( arg1: GE, arg2: GE, arg3: GE, arg4: GE, arg5: GE,
                        arg6: GE, arg7: GE ) : GE =
      make( control, arg1, arg2, arg3, arg4, arg5, arg6, arg7 )
   protected def irExp( arg1: GE, arg2: GE, arg3: GE, arg4: GE, arg5: GE,
                        arg6: GE, arg7: GE ) : GE =
      make( scalar, arg1, arg2, arg3, arg4, arg5, arg6, arg7 )
}