package org.javarosa.xpath.expr

import org.javarosa.core.util.externalizable.DeserializationException
import org.javarosa.core.util.externalizable.ExtUtil
import org.javarosa.core.util.externalizable.ExtWrapNullable
import org.javarosa.core.util.externalizable.Externalizable
import org.javarosa.core.util.externalizable.PrototypeFactory

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

/**
 * An XPathQName is string literal that meets the requirements to be an element or attribute
 * name in an XML document
 */
class XPathQName : Externalizable {
    private var namespace: String? = null

    @JvmField
    var name: String = ""

    private var _hashCode: Int = 0

    constructor() // for deserialization

    constructor(qname: String?) {
        val sep = qname?.indexOf(":") ?: -1
        if (sep == -1) {
            init(null, qname)
        } else {
            init(qname!!.substring(0, sep), qname.substring(sep + 1))
        }
    }

    constructor(namespace: String?, name: String?) {
        init(namespace, name)
    }

    override fun hashCode(): Int {
        return _hashCode
    }

    private fun init(namespace: String?, name: String?) {
        if (name == null
            || name.isEmpty()
            || (namespace != null && namespace.isEmpty())
        ) {
            throw IllegalArgumentException("Invalid QName")
        }

        this.namespace = namespace
        this.name = name
        cacheCode()
    }

    private fun cacheCode() {
        _hashCode = name.hashCode() xor (namespace?.hashCode() ?: 0)
    }

    override fun toString(): String {
        return if (namespace == null) name else "$namespace:$name"
    }

    override fun equals(other: Any?): Boolean {
        if (other is XPathQName) {
            if (_hashCode != other.hashCode()) {
                return false
            }
            return ExtUtil.equals(namespace, other.namespace, false) && name == other.name
        } else {
            return false
        }
    }

    @Throws(IOException::class, DeserializationException::class)
    override fun readExternal(`in`: DataInputStream, pf: PrototypeFactory) {
        namespace = ExtUtil.read(`in`, ExtWrapNullable(String::class.java), pf) as String?
        name = ExtUtil.readString(`in`)
        cacheCode()
    }

    @Throws(IOException::class)
    override fun writeExternal(out: DataOutputStream) {
        ExtUtil.write(out, ExtWrapNullable(namespace))
        ExtUtil.writeString(out, name)
    }
}
