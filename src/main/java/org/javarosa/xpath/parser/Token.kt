package org.javarosa.xpath.parser

class Token @JvmOverloads constructor(
    @JvmField val type: Int,
    @JvmField val `val`: Any? = null
) {

    override fun toString(): String {
        return when (type) {
            AND -> "AND"
            AT -> "AT"
            COMMA -> "COMMA"
            DBL_COLON -> "DBL_COLON"
            DBL_DOT -> "DBL_DOT"
            DBL_SLASH -> "DBL_SLASH"
            DIV -> "DIV"
            DOT -> "DOT"
            EQ -> "EQ"
            GT -> "GT"
            GTE -> "GTE"
            LBRACK -> "LBRACK"
            LPAREN -> "LPAREN"
            LT -> "LT"
            LTE -> "LTE"
            MINUS -> "MINUS"
            MOD -> "MOD"
            MULT -> "MULT"
            NEQ -> "NEQ"
            NSWILDCARD -> "NSWILDCARD($`val`)"
            NUM -> "NUM(${`val`.toString()})"
            OR -> "OR"
            PLUS -> "PLUS"
            QNAME -> "QNAME(${`val`.toString()})"
            RBRACK -> "RBRACK"
            RPAREN -> "RPAREN"
            SLASH -> "SLASH"
            STR -> "STR($`val`)"
            UMINUS -> "UMINUS"
            UNION -> "UNION"
            VAR -> "VAR(${`val`.toString()})"
            WILDCARD -> "WILDCARD"
            else -> "UNKNOWN"
        }
    }

    companion object {
        const val AND = 1
        const val AT = 2
        const val COMMA = 3
        const val DBL_COLON = 4
        const val DBL_DOT = 5
        const val DBL_SLASH = 6
        const val DIV = 7
        const val DOT = 8
        const val EQ = 9
        const val GT = 10
        const val GTE = 11
        const val LBRACK = 12
        const val LPAREN = 13
        const val LT = 14
        const val LTE = 15
        const val MINUS = 16
        const val MOD = 17
        const val MULT = 18
        const val NEQ = 19
        const val NSWILDCARD = 20
        const val NUM = 21
        const val OR = 22
        const val PLUS = 23
        const val QNAME = 24
        const val RBRACK = 25
        const val RPAREN = 26
        const val SLASH = 27
        const val STR = 28
        // Unary minus op
        const val UMINUS = 29
        const val UNION = 30
        const val VAR = 31
        const val WILDCARD = 32
    }
}
