import scala.collection.mutable.ArrayBuffer

/* dsl for  */
class Table:
  val rows = new ArrayBuffer[Row]
  def add(r: Row): Unit = rows += r
  override def toString = rows.mkString("Table(", ", ", ")")

class Row:
  val cells = new ArrayBuffer[Cell]
  def add(c: Cell): Unit = cells += c
  override def toString = cells.mkString("Row(", ", ", ")")

case class Cell(elem: String)

def table(init: Table ?=> Unit): Table =
  given t: Table = new Table()
  init
  t

def row(init: Row ?=> Unit)(using t: Table): Unit =
  given r: Row = new Row()
  init
  t.add(r)

def cell(str: String)(using r: Row) =
  r.add(new Cell(str))

table {
  1 to 3 foreach (i =>
    row {
      Seq("top", " left") foreach (x => cell(x + i))
    }
  )
}

table { ($t: Table) ?=>

  row { ($r: Row) ?=>
    cell("top left")(using $r)
    cell("top right")(using $r)
  }(using $t)

  row { ($r: Row) ?=>
    cell("bottom left")(using $r)
    cell("bottom right")(using $r)
  }(using $t)
}
