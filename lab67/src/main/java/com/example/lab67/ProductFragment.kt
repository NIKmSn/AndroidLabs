package com.example.lab67

import androidx.fragment.app.Fragment

abstract class ProductFragment(var product: ProductCount, var adapter : ProductAdapter) : Fragment() {
    var mustBeRefreshed = true

    abstract fun refresh()
    abstract fun clear()
}