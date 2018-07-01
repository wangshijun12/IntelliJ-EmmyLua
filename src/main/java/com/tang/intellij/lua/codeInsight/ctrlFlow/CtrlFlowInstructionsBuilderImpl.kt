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

package com.tang.intellij.lua.codeInsight.ctrlFlow

import com.tang.intellij.lua.psi.LuaBlock
import com.tang.intellij.lua.psi.LuaLocalFuncDef
import com.tang.intellij.lua.psi.LuaNameDef

class CtrlFlowInstructionsBuilderImpl : CtrlFlowInstructionsBuilder {

    private val pseudoCode = VMPseudoCodeImpl()

    private var scope: VMScope? = null

    override fun <T : VMInstruction> addInstruction(instruction: T): T {
        instruction.scope = scope!!
        pseudoCode.addInstruction(instruction)
        return instruction
    }

    override fun enterScope(block: LuaBlock) {
        scope = VMScope(scope, block)
    }

    override fun exitScope(block: LuaBlock) {
        scope = scope?.parent
    }

    override fun declareParameter(param: LuaNameDef) {
    }

    override fun declareLocalVar(local: LuaNameDef) {
    }

    override fun declareLocalFun(local: LuaLocalFuncDef) {
    }

    override fun returnValue() {
    }
}