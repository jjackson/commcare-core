package org.javarosa.xpath.expr

import org.javarosa.core.model.utils.DateUtils
import org.javarosa.core.util.CacheTable
import org.javarosa.core.util.DataUtil
import org.javarosa.core.util.MathUtils
import org.javarosa.xpath.IExprDataType
import org.javarosa.xpath.XPathNodeset
import org.javarosa.xpath.XPathTypeMismatchException

import java.util.Date
import java.util.HashMap

class FunctionUtils {
    companion object {
        @JvmStatic
        private val funcList: HashMap<String, Class<*>> = HashMap<String, Class<*>>().apply {
            put(XPathDateFunc.NAME, XPathDateFunc::class.java)
            put(XpathCoalesceFunc.NAME, XpathCoalesceFunc::class.java)
            put(XPathTrueFunc.NAME, XPathTrueFunc::class.java)
            put(XPathNowFunc.NAME, XPathNowFunc::class.java)
            put(XPathNumberFunc.NAME, XPathNumberFunc::class.java)
            put(XPathSelectedFunc.NAME, XPathSelectedFunc::class.java)
            put(XPathBooleanFunc.NAME, XPathBooleanFunc::class.java)
            put(XPathLogTenFunc.NAME, XPathLogTenFunc::class.java)
            put(XPathExpFunc.NAME, XPathExpFunc::class.java)
            put(XPathChecklistFunc.NAME, XPathChecklistFunc::class.java)
            put(XPathAtanTwoFunc.NAME, XPathAtanTwoFunc::class.java)
            put(XPathSubstrFunc.NAME, XPathSubstrFunc::class.java)
            put(XPathStringFunc.NAME, XPathStringFunc::class.java)
            put(XPathEndsWithFunc.NAME, XPathEndsWithFunc::class.java)
            put(XPathDependFunc.NAME, XPathDependFunc::class.java)
            put(XPathDoubleFunc.NAME, XPathDoubleFunc::class.java)
            put(XPathTanFunc.NAME, XPathTanFunc::class.java)
            put(XPathReplaceFunc.NAME, XPathReplaceFunc::class.java)
            put(XPathJoinFunc.NAME, XPathJoinFunc::class.java)
            put(XPathFloorFunc.NAME, XPathFloorFunc::class.java)
            put(XPathPiFunc.NAME, XPathPiFunc::class.java)
            put(XPathFormatDateFunc.NAME, XPathFormatDateFunc::class.java)
            put(XPathFormatDateForCalendarFunc.NAME, XPathFormatDateForCalendarFunc::class.java)
            put(XPathMinFunc.NAME, XPathMinFunc::class.java)
            put(XPathSinFunc.NAME, XPathSinFunc::class.java)
            put(XPathBooleanFromStringFunc.NAME, XPathBooleanFromStringFunc::class.java)
            put(XPathCondFunc.NAME, XPathCondFunc::class.java)
            put(XPathSubstringBeforeFunc.NAME, XPathSubstringBeforeFunc::class.java)
            put(XPathCeilingFunc.NAME, XPathCeilingFunc::class.java)
            put(XPathPositionFunc.NAME, XPathPositionFunc::class.java)
            put(XPathStringLengthFunc.NAME, XPathStringLengthFunc::class.java)
            put(XPathRandomFunc.NAME, XPathRandomFunc::class.java)
            put(XPathMaxFunc.NAME, XPathMaxFunc::class.java)
            put(XPathAcosFunc.NAME, XPathAcosFunc::class.java)
            put(XPathAsinFunc.NAME, XPathAsinFunc::class.java)
            put(XPathIfFunc.NAME, XPathIfFunc::class.java)
            put(XPathLowerCaseFunc.NAME, XPathLowerCaseFunc::class.java)
            put(XPathIntFunc.NAME, XPathIntFunc::class.java)
            put(XPathDistanceFunc.NAME, XPathDistanceFunc::class.java)
            put(XPathWeightedChecklistFunc.NAME, XPathWeightedChecklistFunc::class.java)
            put(XPathUpperCaseFunc.NAME, XPathUpperCaseFunc::class.java)
            put(XPathCosFunc.NAME, XPathCosFunc::class.java)
            put(XPathFalseFunc.NAME, XPathFalseFunc::class.java)
            put(XPathLogFunc.NAME, XPathLogFunc::class.java)
            put(XPathRoundFunc.NAME, XPathRoundFunc::class.java)
            put(XPathSubstringAfterFunc.NAME, XPathSubstringAfterFunc::class.java)
            put(XPathAbsFunc.NAME, XPathAbsFunc::class.java)
            put(XPathTranslateFunc.NAME, XPathTranslateFunc::class.java)
            put(XPathCountSelectedFunc.NAME, XPathCountSelectedFunc::class.java)
            put(XPathSelectedAtFunc.NAME, XPathSelectedAtFunc::class.java)
            put(XPathCountFunc.NAME, XPathCountFunc::class.java)
            put(XPathPowFunc.NAME, XPathPowFunc::class.java)
            put(XPathContainsFunc.NAME, XPathContainsFunc::class.java)
            put(XPathNotFunc.NAME, XPathNotFunc::class.java)
            put(XPathSumFunc.NAME, XPathSumFunc::class.java)
            put(XPathRegexFunc.NAME, XPathRegexFunc::class.java)
            put(XPathAtanFunc.NAME, XPathAtanFunc::class.java)
            put(XPathStartsWithFunc.NAME, XPathStartsWithFunc::class.java)
            put(XPathTodayFunc.NAME, XPathTodayFunc::class.java)
            put(XPathConcatFunc.NAME, XPathConcatFunc::class.java)
            put(XPathSqrtFunc.NAME, XPathSqrtFunc::class.java)
            put(XPathUuidFunc.NAME, XPathUuidFunc::class.java)
            put(XPathIdCompressFunc.NAME, XPathIdCompressFunc::class.java)
            put(XPathJoinChunkFunc.NAME, XPathJoinChunkFunc::class.java)
            put(XPathChecksumFunc.NAME, XPathChecksumFunc::class.java)
            put(XPathSortFunc.NAME, XPathSortFunc::class.java)
            put(XPathSortByFunc.NAME, XPathSortByFunc::class.java)
            put(XPathDistinctValuesFunc.NAME, XPathDistinctValuesFunc::class.java)
            put(XPathSleepFunc.NAME, XPathSleepFunc::class.java)
            put(XPathIndexOfFunc.NAME, XPathIndexOfFunc::class.java)
            put(XPathEncryptStringFunc.NAME, XPathEncryptStringFunc::class.java)
            put(XPathDecryptStringFunc.NAME, XPathDecryptStringFunc::class.java)
            put(XPathJsonPropertyFunc.NAME, XPathJsonPropertyFunc::class.java)
            put(XPathClosestPointOnPolygonFunc.NAME, XPathClosestPointOnPolygonFunc::class.java)
            put(XPathIsPointInsidePolygonFunc.NAME, XPathIsPointInsidePolygonFunc::class.java)
        }

        private val mDoubleParseCache: CacheTable<String, Double> = CacheTable()

        /**
         * Gets a human readable string representing an xpath nodeset.
         *
         * @param nodeset An xpath nodeset to be visualized
         * @return A string representation of the nodeset's references
         */
        @JvmStatic
        fun getSerializedNodeset(nodeset: XPathNodeset): String {
            if (nodeset.size() == 1) {
                return toString(nodeset)
            }

            val sb = StringBuffer()
            sb.append("{nodeset: ")
            for (i in 0 until nodeset.size()) {
                val ref = nodeset.getRefAt(i).toString(true)
                sb.append(ref)
                if (i != nodeset.size() - 1) {
                    sb.append(", ")
                }
            }
            sb.append("}")
            return sb.toString()
        }

        /**
         * Take in a value (only a string for now, TODO: Extend?) that doesn't
         * have any type information and attempt to infer a more specific type
         * that may assist in equality or comparison operations
         *
         * @param attrValue A typeless data object
         * @return The passed in object in as specific of a type as was able to
         * be identified.
         */
        @JvmStatic
        fun InferType(attrValue: String): Any {
            // Throwing exceptions from parsing doubles is _very_ slow, which is the purpose
            // of this cache. In high performant situations, this prevents a ton of overhead.
            val d = mDoubleParseCache.retrieve(attrValue)
            if (d != null) {
                return if (d.isNaN()) {
                    attrValue
                } else {
                    d
                }
            }

            try {
                // Don't process strings with scientific notation or +/- Infinity as doubles
                if (checkForInvalidNumericOrDatestringCharacters(attrValue)) {
                    mDoubleParseCache.register(attrValue, java.lang.Double.valueOf(Double.NaN))
                    return attrValue
                }
                val ret = java.lang.Double.parseDouble(attrValue)
                mDoubleParseCache.register(attrValue, ret)
                return ret
            } catch (ife: NumberFormatException) {
                // Not a double
                mDoubleParseCache.register(attrValue, java.lang.Double.valueOf(Double.NaN))
            }
            // TODO: What about dates? That is a _super_ expensive
            // operation to be testing, though...
            return attrValue
        }

        /**
         * convert a value to a boolean using xpath's type conversion rules
         */
        @JvmStatic
        fun toBoolean(o: Any?): Boolean {
            var obj = unpack(o)

            val value: Boolean? = when (obj) {
                is Boolean -> obj
                is Double -> {
                    val d = obj
                    Math.abs(d) > 1.0e-12 && !d.isNaN()
                }
                is String -> obj.isNotEmpty()
                is Date -> true
                is IExprDataType -> obj.toBoolean()
                else -> null
            }

            if (value != null) {
                return value
            } else {
                throw XPathTypeMismatchException("converting to boolean")
            }
        }

        @JvmStatic
        fun toDouble(o: Any?): Double {
            return if (o is Date) {
                DateUtils.fractionalDaysSinceEpoch(o)
            } else {
                toNumeric(o)
            }
        }

        /**
         * Convert a value to a number using xpath's type conversion rules (note that xpath itself makes
         * no distinction between integer and floating point numbers)
         */
        @JvmStatic
        fun toNumeric(o: Any?): Double {
            var obj = unpack(o)

            val value: Double? = when (obj) {
                is Boolean -> if (obj) 1.0 else 0.0
                is Double -> obj
                is String -> {
                    val s = obj.trim()
                    if (checkForInvalidNumericOrDatestringCharacters(s)) {
                        Double.NaN
                    } else {
                        try {
                            java.lang.Double.parseDouble(s)
                        } catch (nfe: NumberFormatException) {
                            try {
                                attemptDateConversion(s)
                            } catch (e: XPathTypeMismatchException) {
                                Double.NaN
                            }
                        }
                    }
                }
                is Date -> DateUtils.daysSinceEpoch(obj).toDouble()
                is IExprDataType -> obj.toNumeric()
                else -> null
            }

            if (value != null) {
                return value
            } else {
                throw XPathTypeMismatchException(
                    "converting '${if (obj == null) "null" else obj.toString()}' to numeric"
                )
            }
        }

        /**
         * The xpath spec doesn't recognize scientific notation, or +/-Infinity when converting a
         * string to a number
         */
        @JvmStatic
        internal fun checkForInvalidNumericOrDatestringCharacters(s: String): Boolean {
            for (i in 0 until s.length) {
                val c = s[i]
                if (c != '-' && c != '.' && (c < '0' || c > '9')) {
                    return true
                }
            }
            return false
        }

        private fun attemptDateConversion(s: String): Double {
            val o = toDate(s)
            if (o is Date) {
                return toNumeric(o)
            } else {
                throw XPathTypeMismatchException()
            }
        }

        /**
         * convert a number to an integer by truncating the fractional part. if non-numeric, coerce the
         * value to a number first. note that the resulting return value is still a Double, as required
         * by the xpath engine
         */
        @JvmStatic
        fun toInt(o: Any?): Double {
            val value = toNumeric(o)

            if (value.isInfinite() || value.isNaN()) {
                return value
            } else if (value >= Long.MAX_VALUE || value <= Long.MIN_VALUE) {
                return value
            } else {
                val l = value.toLong()
                var dbl = l.toDouble()
                if (l == 0L && (value < 0.0 || value == -0.0)) {
                    dbl = -0.0
                }
                return dbl
            }
        }

        /**
         * convert a value to a string using xpath's type conversion rules
         */
        @JvmStatic
        fun toString(o: Any?): String {
            var obj = unpack(o)

            val value: String? = when (obj) {
                is Boolean -> if (obj) "true" else "false"
                is Double -> {
                    val d = obj
                    when {
                        d.isNaN() -> "NaN"
                        Math.abs(d) < 1.0e-12 -> "0"
                        d.isInfinite() -> (if (d < 0) "-" else "") + "Infinity"
                        Math.abs(d - d.toInt()) < 1.0e-12 -> d.toInt().toString()
                        else -> d.toString()
                    }
                }
                is String -> obj
                is Date -> DateUtils.formatDate(obj, DateUtils.FORMAT_ISO8601)
                is IExprDataType -> obj.toString()
                else -> null
            }

            if (value != null) {
                return value
            } else {
                if (obj == null) {
                    throw XPathTypeMismatchException("attempt to cast null value to string")
                } else {
                    throw XPathTypeMismatchException(
                        "converting object of type ${obj.javaClass} to string"
                    )
                }
            }
        }

        /**
         * convert a value to a date. note that xpath has no intrinsic representation of dates, so this
         * is off-spec. dates convert to strings as 'yyyy-mm-dd', convert to numbers as # of days since
         * the unix epoch, and convert to booleans always as 'true'
         *
         * string and int conversions are reversable, however:
         * * cannot convert bool to date
         * * empty string and NaN (xpath's 'null values') go unchanged, instead of being converted
         * into a date (which would cause an error, since Date has no null value (other than java
         * null, which the xpath engine can't handle))
         * * note, however, than non-empty strings that aren't valid dates _will_ cause an error
         * during conversion
         */
        @JvmStatic
        fun toDate(o: Any?): Any {
            val obj = unpack(o)

            if (obj is Double) {
                val n = toInt(obj)

                if (n.isNaN()) {
                    return n
                }

                if (n.isInfinite() || n > Int.MAX_VALUE || n < Int.MIN_VALUE) {
                    throw XPathTypeMismatchException("converting out-of-range value to date")
                }

                return DateUtils.dateAdd(DateUtils.getDate(1970, 1, 1)!!, n.toInt())
            } else if (obj is String) {
                if (obj.isEmpty()) {
                    return obj
                }

                val d = DateUtils.parseDateTime(obj)
                    ?: throw XPathTypeMismatchException("converting string $obj to date")
                return d
            } else if (obj is Date) {
                return DateUtils.roundDate(obj)
            } else {
                val type = if (obj == null) "null" else obj.javaClass.name
                throw XPathTypeMismatchException("converting unexpected type $type to date")
            }
        }

        @JvmStatic
        internal fun expandDateSafe(dateObject: Any?): Date? {
            var obj = dateObject
            if (obj !is Date) {
                // try to expand this out of a nodeset
                obj = toDate(obj)
            }
            return if (obj is Date) {
                obj
            } else {
                null
            }
        }

        @JvmStatic
        internal fun subsetArgList(args: Array<Any?>, start: Int): Array<Any?> {
            return subsetArgList(args, start, 1)
        }

        /**
         * return a subset of an argument list as a new arguments list
         *
         * @param start index to start at
         * @param skip  sub-list will contain every nth argument, where n == skip (default: 1)
         */
        @JvmStatic
        internal fun subsetArgList(args: Array<Any?>, start: Int, skip: Int): Array<Any?> {
            if (start > args.size || skip < 1) {
                throw RuntimeException("error in subsetting arglist")
            }

            val subargs = arrayOfNulls<Any>(
                MathUtils.divLongNotSuck((args.size - start - 1).toLong(), skip.toLong()).toInt() + 1
            )
            var j = 0
            var i = start
            while (i < args.size) {
                subargs[j] = args[i]
                i += skip
                j++
            }

            return subargs
        }

        @JvmStatic
        fun unpack(o: Any?): Any? {
            return if (o is XPathNodeset) {
                o.unpack()
            } else {
                o
            }
        }

        /**
         * Perform toUpperCase or toLowerCase on given object.
         */
        @JvmStatic
        internal fun normalizeCase(o: Any?, toUpper: Boolean): String {
            val s = toString(o)
            return if (toUpper) {
                s.uppercase()
            } else {
                s.lowercase()
            }
        }

        /**
         * @return A sequence representation of the input, whether the input is
         * a nodeset (which will be dereferenced and evaluated), an existing sequence,
         * or a string representation of a sequence (space separated list of strings)
         */
        @JvmStatic
        fun getSequence(input: Any?): Array<Any> {
            return when (input) {
                is XPathNodeset -> @Suppress("UNCHECKED_CAST") (input.toArgList() as Array<Any>)
                is Array<*> -> @Suppress("UNCHECKED_CAST") (input as Array<Any>)
                else -> {
                    val selection = unpack(input) as String
                    @Suppress("UNCHECKED_CAST")
                    (DataUtil.splitOnSpaces(selection) as Array<Any>)
                }
            }
        }

        /**
         * Get list of base xpath functions
         *
         * (Used in formplayer for function auto-completion)
         */
        @Suppress("unused")
        @JvmStatic
        fun xPathFuncList(): List<String> {
            return ArrayList(funcList.keys)
        }

        @JvmStatic
        fun getXPathFuncListMap(): HashMap<String, Class<*>> {
            return funcList
        }
    }
}
