// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.grazie.ide.ui.components.rules

import com.intellij.grazie.jlanguage.Lang
import com.intellij.ui.CheckedTreeNode
import com.intellij.ui.SimpleTextAttributes
import java.util.*

@Suppress("EqualsOrHashCode")
class GrazieTreeNode(userObject: Any? = null) : CheckedTreeNode(userObject) {
  val nodeText: String
    get() = when (val meta = userObject) {
      is RuleWithLang -> meta.rule.description //+ " " + meta.rule.id
      is ComparableCategory -> meta.category.getName(meta.lang.jLanguage) //+ " " + meta.id
      is Lang -> meta.displayName
      else -> ""
    }

  val attrs: SimpleTextAttributes
    get() = if (userObject is RuleWithLang) SimpleTextAttributes.REGULAR_ATTRIBUTES else SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES

  fun resetMark(state: HashMap<String, RuleWithLang>): Boolean {
    val meta = userObject
    if (meta is RuleWithLang) {
      isChecked = when (val rule = state[meta.rule.id]) {
        is RuleWithLang -> rule.enabledInTree
        else -> meta.enabled
      }
    }
    else {
      isChecked = false
      for (child in children()) {
        if (child is GrazieTreeNode && child.resetMark(state)) {
          isChecked = true
        }
      }
    }

    return isChecked
  }

  override fun equals(other: Any?): Boolean {
    if (other is GrazieTreeNode) {
      return userObject == other.userObject
    }

    return super.equals(other)
  }
}
