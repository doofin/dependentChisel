package dependentChisel

object api {
  export dependentChisel.typesAndSyntax.chiselModules.*
  export dependentChisel.typesAndSyntax.varDecls.*
  export dependentChisel.typesAndSyntax.typesAndOps.*
  export dependentChisel.typesAndSyntax.statements.*
  export dependentChisel.codegen.compiler.*
}

/* object aa {
  inline def newIO[w <: Int](using
      i1: Int
  )(tp: Float, givenName: String = "") = {}
}

object bm { export aa.* }
object bb {
  import bm.*
  def a = {
    given a2: Int = 2
    newIO(1)
  }
} */
