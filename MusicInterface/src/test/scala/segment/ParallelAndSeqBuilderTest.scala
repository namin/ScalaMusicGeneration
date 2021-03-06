package segment

import org.scalatest.FunSuite
import segmentSystem.SequentialSegment
import segmentSystem.MusicalSegment
import utils.MelodyWriter
import segmentSystem.ClassPredicate
import tonalSystem.Tone._
import segmentSystem.ClassPredicate.isSeq

class ParallelAndSeqBuilderTest extends FunSuite with MelodyWriter {


  implicit val sequentialBuilder: List[MS] => SS = ALevel(_)
  val melody = I ++ I ++ I ++ I ++ I
  //             A    B    C    Seq
  
  val song = (I ++ I) ++ (I ++ I).swapTo(AbisLevel(_))
  //            A     B     Abis
  
  test("ALevel selection") {
    val after = melody mapIf (IsA thenDo (_ + 1))
    val expected = II ++ II ++ I ++ I ++ I
    assert(after == expected)
  }
  
  test("BLevel selection") {
    val after = melody mapIf (IsB thenDo (_ + 2))
    val expected = III ++ III ++ III ++ I ++ I
    assert(after == expected)
  }
  
  test("CLevel selection") {
    val after = melody mapIf (IsC thenDo (_ + 3))
    val expected = IV ++ IV ++ IV ++ IV ++ I
    assert(after == expected)
  }
  
  test("Seq default top level selection") {
    val after = melody mapIf (isSeq thenDo (_ + 4))
    val expected = V ++ V ++ V ++ V ++ V
    assert(after == expected)
  }
  
  test("A but not Abis selection") {
    val after = song mapIf (IsA thenDo (_ + 1))
    val expected = (II ++ II) ++ (I ++ I).swapTo(AbisLevel(_))
    assert(after == expected)
  }
  
  test("Abis but not A selection") {
    val after = song mapIf (IsAbis thenDo (_ + 2))
    val expected = (I ++ I) ++ (III ++ III).swapTo(AbisLevel(_))
    assert(after == expected)
  }
  
  test("Common holder for A, Abis") {
    val after = song mapIf (IsB thenDo (_ + 3))
    val expected = (IV ++ IV) ++ (IV ++ IV).swapTo(AbisLevel(_))
    assert(after == expected)
  }
}


case class ALevel(melody: List[MusicalSegment]) extends SequentialSegment {
  override val buildFromMelody = ALevel(_)
  override val sequentialBuilder = BLevel(_)
}

case class AbisLevel(melody: List[MusicalSegment]) extends SequentialSegment {
  override val buildFromMelody = AbisLevel(_)
  override val sequentialBuilder = BLevel(_)
}

case class BLevel(melody: List[MusicalSegment]) extends SequentialSegment {
  override val buildFromMelody = BLevel(_)
  override val sequentialBuilder = CLevel(_)
}

case class CLevel(melody: List[MusicalSegment]) extends SequentialSegment {
  override val buildFromMelody = CLevel(_)
}

object IsA extends ClassPredicate[ALevel](_ match {case n: ALevel => n})
object IsAbis extends ClassPredicate[AbisLevel](_ match {case n: AbisLevel => n})
object IsB extends ClassPredicate[BLevel](_ match {case n: BLevel => n})
object IsC extends ClassPredicate[CLevel](_ match {case n: CLevel => n})