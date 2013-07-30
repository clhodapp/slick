

package com.typesafe.slick.testkit.tests

import com.typesafe.slick.testkit.util.{RelationalTestDB, TestkitTest}
import org.junit.Assert._

class ReducedTest extends TestkitTest[RelationalTestDB] {
  import tdb.profile.simple._

	def testQuery {

		class Suppliers(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "SUPPLIERS") {
			def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
			def name = column[String]("SUP_NAME")
			def street = column[String]("STREET")
			def city = column[String]("CITY")
			def state = column[String]("STATE")
			def zip = column[String]("ZIP")
			// Every table needs a * projection with the same type as the table's type parameter
			def * = (id, name, street, city, state, zip)
		}

		val suppliers = TableQuery[Suppliers]

		class Coffees(tag: Tag) extends Table[(String, Int, Double, Int, Int)](tag, "COFFEES") {
			def name = column[String]("COF_NAME", O.PrimaryKey)
			def supID = column[Int]("SUP_ID")
			def price = column[Double]("PRICE")
			def sales = column[Int]("SALES")
			def total = column[Int]("TOTAL")
			def * = (name, supID, price, sales, total)
		}

		val coffees = TableQuery[Coffees]

		(suppliers.ddl ++ coffees.ddl).create

		val query2 = 
			(coffees rightJoin suppliers on (_.supID === _.id)).map { case (c, s) =>
				val name = s.name
				(c, s, name)
			}.groupBy { prop =>
				val c = prop._1
				val s = prop._2
				val name = prop._3
				s.id
			}.map { prop =>
				val supId = prop._1
				val c = prop._2.map(x => x._1)
				val s = prop._2.map(x => x._2)
				val name = prop._2.map(x => x._3)
				(s.map(_.name).min, supId, c.length)
			}

		assert(query2.run.toList.isEmpty)
	}

}
