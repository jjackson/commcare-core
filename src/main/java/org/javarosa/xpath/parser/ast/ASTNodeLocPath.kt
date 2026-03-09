package org.javarosa.xpath.parser.ast

import org.javarosa.xpath.expr.XPathExpression
import org.javarosa.xpath.expr.XPathFilterExpr
import org.javarosa.xpath.expr.XPathPathExpr
import org.javarosa.xpath.expr.XPathStep
import org.javarosa.xpath.parser.Token
import org.javarosa.xpath.parser.XPathSyntaxException
import java.util.Vector

class ASTNodeLocPath : ASTNode() {

    @JvmField
    val clauses: Vector<ASTNode> = Vector()

    @JvmField
    var separators: List<Int> = ArrayList()

    override fun getChildren(): Vector<ASTNode> {
        return clauses
    }

    fun isAbsolute(): Boolean {
        return (clauses.size == separators.size) || (clauses.size == 0 && separators.size == 1)
    }

    @Throws(XPathSyntaxException::class)
    override fun build(): XPathExpression {
        val steps = Vector<XPathStep>()
        var filtExpr: XPathExpression? = null
        val offset = if (isAbsolute()) 1 else 0

        for (i in 0 until clauses.size + offset) {
            if (offset == 0 || i > 0) {
                val clause = clauses.elementAt(i - offset)
                if (clause is ASTNodePathStep) {
                    steps.addElement(clause.getStep())
                } else {
                    filtExpr = clause.build()
                }
            }

            if (i < separators.size) {
                if (separators[i] == Token.DBL_SLASH) {
                    steps.addElement(XPathStep.ABBR_DESCENDANTS())
                }
            }
        }

        val stepArr = Array<XPathStep>(steps.size) { i -> steps.elementAt(i) }

        return if (filtExpr == null) {
            XPathPathExpr(
                if (isAbsolute()) XPathPathExpr.INIT_CONTEXT_ROOT else XPathPathExpr.INIT_CONTEXT_RELATIVE,
                stepArr
            )
        } else {
            if (filtExpr is XPathFilterExpr) {
                XPathPathExpr(filtExpr, stepArr)
            } else {
                XPathPathExpr(XPathFilterExpr(filtExpr, emptyArray()), stepArr)
            }
        }
    }
}
