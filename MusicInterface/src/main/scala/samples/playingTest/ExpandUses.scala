package playingTest

import utils.MelodyWriter
import tonalSystem.Tone._
import segmentSystem.ClassPredicate.isNote
import rythmics.E
import segmentSystem.Note
import segmentSystem.MusicalSegment
import midiInterface.MelodyPlayer
import tonalSystem.Major
import tonalSystem.C
import segmentSystem.ClassPredicate.isSeq

object ExpandUses extends App with MelodyWriter {
  
  type T = MusicalSegment => MusicalSegment
  val tempo = 60
  val scale = Major(C)
  
  val base = I * 4
  
  
  val t0: T = _ *2 /2
  val t1: T = _ /2 fillSeq (_ +4)
  val t2: T = _ /2 fillSeq (_ / 2 *2 +4)
  
//  play(base)
  
  
  val step1 = base mapIf (isNote thenDo (t0, 2))
  
  val step2 = step1 mapIf (isSeq given (_.height == 1) thenDo (t2, 2, 1) orDo (t1))
  
  play(step2)
  
  def play(m: MS) = MelodyPlayer(m withScale scale, tempo)
}