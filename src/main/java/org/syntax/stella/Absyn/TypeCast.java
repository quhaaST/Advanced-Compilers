// File generated by the BNF Converter (bnfc 2.9.4.1).

package org.syntax.stella.Absyn;

public class TypeCast  extends Expr {
  public final Expr expr_;
  public final Type type_;
  public int line_num, col_num, offset;
  public TypeCast(Expr p1, Type p2) { expr_ = p1; type_ = p2; }

  public <R,A> R accept(Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof TypeCast) {
      TypeCast x = (TypeCast)o;
      return this.expr_.equals(x.expr_) && this.type_.equals(x.type_);
    }
    return false;
  }

  public int hashCode() {
    return 37*(this.expr_.hashCode())+this.type_.hashCode();
  }


}
