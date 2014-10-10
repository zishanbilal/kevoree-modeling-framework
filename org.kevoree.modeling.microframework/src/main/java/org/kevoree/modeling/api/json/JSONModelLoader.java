package org.kevoree.modeling.api.json;

import java.io.InputStream;

import org.kevoree.modeling.api.Callback;
import org.kevoree.modeling.api.KObject;
import java.util.ArrayList;
import java.util.List;

import org.kevoree.modeling.api.util.ActionType;
import org.kevoree.modeling.api.KFactory;
import org.kevoree.modeling.api.ModelLoader;
import org.kevoree.modeling.api.util.Converters;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 28/08/13
 * Time: 13:08
 */
public class JSONModelLoader implements ModelLoader {
        private KFactory factory;

    public JSONModelLoader(KFactory factory) {
        this.factory = factory;
    }

    // TODO asynchronous implementation
    @Override
    public void loadModelFromString(String str, Callback<KObject> callback, Callback<Exception> error) {
        deserialize(new Converters().byteArrayInputStreamFromString(str));
    }

    // TODO asynchronous implementation
    @Override
    public void loadModelFromStream(InputStream inputStream, Callback<KObject> callback, Callback<Exception> error) {
        deserialize(inputStream);
    }

    private List<KObject> deserialize(InputStream instream) {
        if (instream == null) {
            throw new RuntimeException("Null input Stream");
        }
        ArrayList<ResolveCommand> resolverCommands = new ArrayList<ResolveCommand>();
        ArrayList<KObject> roots =  new ArrayList<KObject>();
        Lexer lexer = Lexer(instream);
        boolean currentToken = lexer.nextToken();
        if (currentToken.tokenType == org.kevoree.modeling.api.json.Type.LEFT_BRACE) {
            loadObject(lexer, null, null, roots, resolverCommands)
        } else {
            throw Exception("Bad Format / { expected")
        }
        for (resol in resolverCommands) {
            resol.run()
        }
        return roots
    }


    fun loadObject(lexer: Lexer, nameInParent: String?, parent: KObject?, roots: ArrayList<KObject>, commands: ArrayList<ResolveCommand>) {
        //must ne currently on { at input
        var currentToken = lexer.nextToken()
        var currentObject: KObject? = null
        if (currentToken.tokenType == org.kevoree.modeling.api.json.Type.VALUE) {
            if (currentToken.value == "class") {
                lexer.nextToken() //unpop :
                currentToken = lexer.nextToken() //Two step for having the name
                var name = currentToken.value?.toString()!!
                var typeName: String? = null
                var isRoot = false
                if (name.startsWith("root:")) {
                    isRoot = true
                    name = name.substring("root:".length)
                }
                if (name.contains("@")) {
                    typeName = name.substring(0, name.indexOf("@"))
                    val key = name.substring(name.indexOf("@") + 1)
                    if (parent == null) {
                        if (isRoot) {
                            currentObject = factory.lookup("/")
                        }
                    } else {
                        val path = parent.path() + "/" + nameInParent + "[" + key + "]"
                        currentObject = factory.lookup(path)
                    }
                } else {
                    typeName = name
                }
                if (currentObject == null) {
                    currentObject = factory.create(typeName!!)
                }
                if (isRoot) {
                    factory.root(currentObject!!)
                }
                if (parent == null) {
                    roots.add(currentObject!!)
                }
                //next loop while begin a sub Elem
                var currentNameAttOrRef: String? = null
                var refModel = false
                currentToken = lexer.nextToken()
                while (currentToken.tokenType != org.kevoree.modeling.api.json.Type.EOF) {
                    if (currentToken.tokenType == org.kevoree.modeling.api.json.Type.LEFT_BRACE) {
                        loadObject(lexer, currentNameAttOrRef!!, currentObject, roots, commands)
                    }
                    if (currentToken.tokenType == org.kevoree.modeling.api.json.Type.COMMA) {
                        //ignore
                    }
                    if (currentToken.tokenType == org.kevoree.modeling.api.json.Type.VALUE) {
                        if (currentNameAttOrRef == null) {
                            currentNameAttOrRef = currentToken.value.toString()
                        } else {
                            if (refModel) {
                                commands.add(ResolveCommand(roots, currentToken.value!!.toString(), currentObject!!, currentNameAttOrRef!!))
                            } else {
                                val unscaped = JSONString.unescape(currentToken.value.toString())
                                currentObject!!.reflexiveMutator(ActionType.SET, currentNameAttOrRef!!, unscaped, false, false)
                                currentNameAttOrRef = null //unpop
                            }
                        }
                    }
                    if (currentToken.tokenType == org.kevoree.modeling.api.json.Type.LEFT_BRACKET) {
                        currentToken = lexer.nextToken()
                        if (currentToken.tokenType == org.kevoree.modeling.api.json.Type.LEFT_BRACE) {
                            loadObject(lexer, currentNameAttOrRef!!, currentObject, roots, commands)
                        } else {
                            refModel = true //wait for all ref to be found
                            if (currentToken.tokenType == org.kevoree.modeling.api.json.Type.VALUE) {
                                commands.add(ResolveCommand(roots, currentToken.value!!.toString(), currentObject!!, currentNameAttOrRef!!))
                            }
                        }
                    }
                    if (currentToken.tokenType == org.kevoree.modeling.api.json.Type.RIGHT_BRACKET) {
                        currentNameAttOrRef = null //unpop
                        refModel = false
                    }
                    if (currentToken.tokenType == org.kevoree.modeling.api.json.Type.RIGHT_BRACE) {
                        if (parent != null) {
                            parent.reflexiveMutator(ActionType.ADD, nameInParent!!, currentObject, false, false)
                        }
                        return //go out
                    }
                    currentToken = lexer.nextToken()
                }
            } else {
                throw Exception("Bad Format / eClass att must be first")
                //TODO save temp att
            }
        } else {
            throw Exception("Bad Format")
        }
    }

}

class ResolveCommand {
    private ArrayList<KObject> roots;
    private String ref;
    private

    public ResolveCommand(ArrayList<KObject> roots, String ref, KObject currentRootElem, String refName) {

    }

    fun run() {
        var referencedElement:Any ? = null
        var i = 0
        while (referencedElement == null && i < roots.size()) {
            referencedElement = roots.get(i++).findByPath(ref)
        }
        if (referencedElement != null) {
            currentRootElem.reflexiveMutator(ActionType.ADD, refName, referencedElement, false, false)
        } else {
            throw Exception("Unresolved " + ref)
        }
    }
}
