// File generated by the BNF Converter (bnfc 2.9.4.1).

package org.syntax.stella.Absyn;

public class NoReturnType  extends ReturnType {
  public int line_num, col_num, offset;
  public NoReturnType() { }

  public <R,A> R accept(Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof NoReturnType) {
      return true;
    }
    return false;
  }

  public int hashCode() {
    return 37;
  }


}
