

package com.typesafe.slick.testkit.tests

import com.typesafe.slick.testkit.util.{RelationalTestDB, TestkitTest}
import org.junit.Assert._

class ReducedTest extends TestkitTest[RelationalTestDB] {
  import tdb.profile.simple._

	def testQuery {

		class A(tag: Tag) extends Table[(Int, String)](tag, "A") {
			def id = column[Int]("id", O.PrimaryKey)
			def name = column[String]("name")
			def * = (id, name)
		}

		val a = TableQuery[A]


		a.ddl.create

		val query2 = a.groupBy { prop =>
			val s = prop
			s.id
		}.map { prop => 
			val id = prop._1
			val s = prop._2.map(x => x)

			s.map(x => x.name).min

		}

		assert(query2.run.toList.isEmpty)
	}

}

