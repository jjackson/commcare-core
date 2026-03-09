package org.javarosa.xpath.parser.ast

import org.javarosa.xpath.expr.XPathExpression
import org.javarosa.xpath.expr.XPathNumNegExpr
import org.javarosa.xpath.expr.XPathUnaryOpExpr
import org.javarosa.xpath.parser.Token
import org.javarosa.xpath.parser.XPathSyntaxException

class ASTNodeUnaryOp : ASTNode() {

    @JvmField
    var expr: ASTNode? = null

    @JvmField
    var op: Int = 0

    override fun getChildren(): List<ASTNode> {
        val v = ArrayList<ASTNode>()
        expr?.let { v.add(it) }
        return v
    }

    @Throws(XPathSyntaxException::class)
    override fun build(): XPathExpression {
        val x: XPathUnaryOpExpr = if (op == Token.UMINUS) {
            XPathNumNegExpr(expr!!.build()!!)
        } else {
            throw XPathSyntaxException()
        }
        return x
    }
}
