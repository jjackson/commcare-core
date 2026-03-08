package org.javarosa.core.model.util.restorable

import org.javarosa.core.model.Constants
import org.javarosa.core.model.condition.EvaluationContext
import org.javarosa.core.model.condition.IConditionExpr
import org.javarosa.core.model.instance.FormInstance
import org.javarosa.core.model.instance.TreeReference
import org.javarosa.core.services.storage.Persistable
import org.javarosa.model.xform.XPathReference
import org.javarosa.xpath.XPathConditional
import org.javarosa.xpath.expr.XPathPathExpr
import java.util.Date
import java.util.Vector

object RestoreUtils {
    @JvmField
    val RECORD_ID_TAG: String = "rec-id"

    @JvmStatic
    fun ref(refStr: String): TreeReference {
        return FormInstance.unpackReference(XPathReference(refStr))
    }

    @JvmStatic
    fun refToPathExpr(ref: TreeReference): IConditionExpr {
        return XPathConditional(XPathPathExpr.fromRef(ref))
    }

    private fun topRef(dm: FormInstance): TreeReference {
        return ref("/" + dm.root.name)
    }

    private fun childRef(childPath: String, parentRef: TreeReference): TreeReference {
        return ref(childPath).parent(parentRef)
    }

    //used for incoming data
    private fun getDataType(c: Class<*>): Int {
        return when (c) {
            String::class.java -> Constants.DATATYPE_TEXT
            Integer::class.java, Int::class.java -> Constants.DATATYPE_INTEGER
            java.lang.Long::class.java, Long::class.java -> Constants.DATATYPE_LONG
            java.lang.Float::class.java, Float::class.java,
            java.lang.Double::class.java, Double::class.java -> Constants.DATATYPE_DECIMAL
            Date::class.java -> Constants.DATATYPE_DATE
            java.lang.Boolean::class.java, Boolean::class.java -> Constants.DATATYPE_TEXT
            else -> throw RuntimeException("Can't handle data type ${c.name}")
        }
    }

    @JvmStatic
    fun getValue(xpath: String, tree: FormInstance): Any? {
        val context = topRef(tree)
        val node = tree.resolveReference(ref(xpath).contextualize(context))
            ?: throw RuntimeException("Could not find node [$xpath] when parsing saved instance!")

        return if (node.isRelevant) {
            val value = node.value
            value?.value
        } else {
            null
        }
    }

    @JvmStatic
    fun applyDataType(dm: FormInstance, path: String, parent: TreeReference, type: Class<*>) {
        val dataType = getDataType(type)
        val ref = childRef(path, parent)

        val v: Vector<TreeReference> = EvaluationContext(dm).expandReference(ref)
        for (i in 0 until v.size) {
            val e = dm.resolveReference(v.elementAt(i))
            e.dataType = dataType
        }
    }

    @JvmStatic
    fun templateData(r: Restorable, dm: FormInstance, parent: TreeReference?) {
        var resolvedParent = parent
        if (resolvedParent == null) {
            resolvedParent = topRef(dm)
            applyDataType(dm, "timestamp", resolvedParent, Date::class.java)
        }

        if (r is Persistable) {
            applyDataType(dm, RECORD_ID_TAG, resolvedParent, Integer::class.java)
        }

        r.templateData(dm, resolvedParent)
    }
}
