

package com.typesafe.slick.testkit.tests

import org.junit.Assert._
import com.typesafe.slick.testkit.util.{TestkitTest, TestDB}

class ReducedTest(val tdb: TestDB) extends TestkitTest {
  import tdb.profile.simple._

	def testQuery {

		class b_table extends Table[(Long, String, String)]("b_table") {
			def id = column[Long]("id", O.PrimaryKey)
			def b = column[String]("b")
			def d = column[String]("d")

			def * = id ~ b ~ d
		}

		val b_table = new b_table

		class a_table extends Table[(Long, String, Long, Long)]("a_table") { 
			def id = column[Long]("id", O.PrimaryKey)
			def a = column[String]("a")
			def c = column[Long]("c")
			def fkId = column[Long]("fkId")

			def idConstraint = foreignKey("id_fk", fkId, b_table)(_.id)
			
			def * = id ~ a ~ c ~ fkId

		}


		val a_table = new a_table

		(a_table.ddl ++ b_table.ddl).create

    def query4(param: String) = a_table.flatMap { t1 =>
			b_table.withFilter { t2 =>
				t1.fkId === t2.id && t2.d === param
			}.map(t2 => (t1, t2))
		}.groupBy { prop => 
			val t1 = prop._1
			val t2 = prop._2
			(t1.a, t2.b)
		}.map { prop => 
      val a = prop._1._1
      val b = prop._1._2
      val t1 = prop._2.map(_._1)
      val t2 = prop._2.map(_._2)
      val c3 = t1.map(_.c).max
      scala.Tuple3(a, b, c3)
    }

		assert(query4("").run.toList.isEmpty)
	}

}
