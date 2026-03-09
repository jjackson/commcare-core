package org.javarosa.xpath

import org.javarosa.xpath.expr.XPathExpression
import org.javarosa.xpath.parser.Lexer
import org.javarosa.xpath.parser.Parser
import org.javarosa.xpath.parser.XPathSyntaxException

class XPathParseTool {
    companion object {
        @JvmStatic
        @Throws(XPathSyntaxException::class)
        fun parseXPath(xpath: String?): XPathExpression {
            return Parser.parse(Lexer.lex(xpath))
        }
    }
}
