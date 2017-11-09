/*
 * Copyright (c) 2017. tangzx(love.tangzx@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tang.intellij.lua.debugger.attach.value

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.psi.PsiManager
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.frame.XNavigatable
import com.intellij.xdebugger.frame.XValue
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace
import com.intellij.xdebugger.impl.XSourcePositionImpl
import com.tang.intellij.lua.debugger.attach.LuaAttachDebugProcess
import com.tang.intellij.lua.debugger.attach.readString
import com.tang.intellij.lua.psi.LuaPsiTreeUtil
import org.w3c.dom.Node
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import javax.xml.parsers.DocumentBuilderFactory

enum class StackNodeId
{
    List,
    Eval,
    StackRoot,

    Table,
    Function,
    UserData,
    String,
    Binary,
    Primitive,

    Error,
}

/**
 *
 * Created by tangzx on 2017/4/2.
 */
abstract class LuaXValue(override val L:Long,
                         override val process: LuaAttachDebugProcess) : XValue(), IStackNode {

    var name: String? = null
    var parent: LuaXValue? = null

    lateinit var type: String

    override fun computePresentation(xValueNode: XValueNode, xValuePlace: XValuePlace) {

    }

    open fun toKeyString(): String {
        return toString()
    }

    override fun computeSourcePosition(xNavigable: XNavigatable) {
        if (name != null) {
            computeSourcePosition(xNavigable, name!!, process.session)
        }
    }

    override fun read(stream: DataInputStream) {
        name = stream.readString()
        type = stream.readString()
    }

    companion object {
        fun computeSourcePosition(xNavigable: XNavigatable, name: String, session: XDebugSession) {
            val currentPosition = session.currentPosition
            if (currentPosition != null) {
                val file = currentPosition.file
                val project = session.project
                val psiFile = PsiManager.getInstance(project).findFile(file)
                val editor = FileEditorManager.getInstance(project).getSelectedEditor(file)

                if (psiFile != null && editor is TextEditor) {
                    val document = editor.editor.document
                    val lineEndOffset = document.getLineStartOffset(currentPosition.line)
                    val element = psiFile.findElementAt(lineEndOffset)
                    LuaPsiTreeUtil.walkUpLocalNameDef(element) { nameDef ->
                        if (name == nameDef.name) {
                            val position = XSourcePositionImpl.createByElement(nameDef)
                            xNavigable.setSourcePosition(position)
                            return@walkUpLocalNameDef false
                        }
                        true
                    }
                }
            }
        }
    }
}

open class LuaXObjectValue(val id: StackNodeId, L: Long, process: LuaAttachDebugProcess)
    : LuaXValue(L, process)
