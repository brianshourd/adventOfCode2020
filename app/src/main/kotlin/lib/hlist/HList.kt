package lib.hlist

class HList1<T1>(val head: T1) {
    fun <T> prepend(t: T): HList2<T, T1> = HList2(t, this)
    operator fun component1(): T1 = head
}
class HList2<T1, T2>(val head: T1, val tail: HList1<T2>) {
    fun <T> prepend(t: T): HList3<T, T1, T2> = HList3(t, this)
    operator fun component1(): T1 = head
    operator fun component2(): T2 = tail.head
}
class HList3<T1, T2, T3>(val head: T1, val tail: HList2<T2, T3>) {
    fun <T> prepend(t: T): HList4<T, T1, T2, T3> = HList4(t, this)
    operator fun component1(): T1 = head
    operator fun component2(): T2 = tail.head
    operator fun component3(): T3 = tail.tail.head
}
class HList4<T1, T2, T3, T4>(val head: T1, val tail: HList3<T2, T3, T4>) {
    fun <T> prepend(t: T): HList5<T, T1, T2, T3, T4> = HList5(t, this)
    operator fun component1(): T1 = head
    operator fun component2(): T2 = tail.head
    operator fun component3(): T3 = tail.tail.head
    operator fun component4(): T4 = tail.tail.tail.head
}
class HList5<T1, T2, T3, T4, T5>(val head: T1, val tail: HList4<T2, T3, T4, T5>) {
    fun <T> prepend(t: T): HList6<T, T1, T2, T3, T4, T5> = HList6(t, this)
    operator fun component1(): T1 = head
    operator fun component2(): T2 = tail.head
    operator fun component3(): T3 = tail.tail.head
    operator fun component4(): T4 = tail.tail.tail.head
    operator fun component5(): T5 = tail.tail.tail.tail.head
}
class HList6<T1, T2, T3, T4, T5, T6>(val head: T1, val tail: HList5<T2, T3, T4, T5, T6>) {
    fun <T> prepend(t: T): HList7<T, T1, T2, T3, T4, T5, T6> = HList7(t, this)
    operator fun component1(): T1 = head
    operator fun component2(): T2 = tail.head
    operator fun component3(): T3 = tail.tail.head
    operator fun component4(): T4 = tail.tail.tail.head
    operator fun component5(): T5 = tail.tail.tail.tail.head
    operator fun component6(): T6 = tail.tail.tail.tail.tail.head
}
class HList7<T1, T2, T3, T4, T5, T6, T7>(val head: T1, val tail: HList6<T2, T3, T4, T5, T6, T7>) {
    fun <T> prepend(t: T): HList8<T, T1, T2, T3, T4, T5, T6, T7> = HList8(t, this)
    operator fun component1(): T1 = head
    operator fun component2(): T2 = tail.head
    operator fun component3(): T3 = tail.tail.head
    operator fun component4(): T4 = tail.tail.tail.head
    operator fun component5(): T5 = tail.tail.tail.tail.head
    operator fun component6(): T6 = tail.tail.tail.tail.tail.head
    operator fun component7(): T7 = tail.tail.tail.tail.tail.tail.head
}
class HList8<T1, T2, T3, T4, T5, T6, T7, T8>(val head: T1, val tail: HList7<T2, T3, T4, T5, T6, T7, T8>) {
    fun <T> prepend(t: T): HList9<T, T1, T2, T3, T4, T5, T6, T7, T8> = HList9(t, this)
    operator fun component1(): T1 = head
    operator fun component2(): T2 = tail.head
    operator fun component3(): T3 = tail.tail.head
    operator fun component4(): T4 = tail.tail.tail.head
    operator fun component5(): T5 = tail.tail.tail.tail.head
    operator fun component6(): T6 = tail.tail.tail.tail.tail.head
    operator fun component7(): T7 = tail.tail.tail.tail.tail.tail.head
    operator fun component8(): T8 = tail.tail.tail.tail.tail.tail.tail.head
}
class HList9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(val head: T1, val tail: HList8<T2, T3, T4, T5, T6, T7, T8, T9>) {
    fun <T> prepend(t: T): HList10<T, T1, T2, T3, T4, T5, T6, T7, T8, T9> = HList10(t, this)
    operator fun component1(): T1 = head
    operator fun component2(): T2 = tail.head
    operator fun component3(): T3 = tail.tail.head
    operator fun component4(): T4 = tail.tail.tail.head
    operator fun component5(): T5 = tail.tail.tail.tail.head
    operator fun component6(): T6 = tail.tail.tail.tail.tail.head
    operator fun component7(): T7 = tail.tail.tail.tail.tail.tail.head
    operator fun component8(): T8 = tail.tail.tail.tail.tail.tail.tail.head
    operator fun component9(): T9 = tail.tail.tail.tail.tail.tail.tail.tail.head
}
class HList10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(val head: T1, val tail: HList9<T2, T3, T4, T5, T6, T7, T8, T9, T10>) {
    fun <T> prepend(t: T): HList11<T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = HList11(t, this)
    operator fun component1(): T1 = head
    operator fun component2(): T2 = tail.head
    operator fun component3(): T3 = tail.tail.head
    operator fun component4(): T4 = tail.tail.tail.head
    operator fun component5(): T5 = tail.tail.tail.tail.head
    operator fun component6(): T6 = tail.tail.tail.tail.tail.head
    operator fun component7(): T7 = tail.tail.tail.tail.tail.tail.head
    operator fun component8(): T8 = tail.tail.tail.tail.tail.tail.tail.head
    operator fun component9(): T9 = tail.tail.tail.tail.tail.tail.tail.tail.head
    operator fun component10(): T10 = tail.tail.tail.tail.tail.tail.tail.tail.tail.head
}
class HList11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>(val head: T1, val tail: HList10<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>) {
    fun <T> prepend(t: T): HList12<T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> = HList12(t, this)
    operator fun component1(): T1 = head
    operator fun component2(): T2 = tail.head
    operator fun component3(): T3 = tail.tail.head
    operator fun component4(): T4 = tail.tail.tail.head
    operator fun component5(): T5 = tail.tail.tail.tail.head
    operator fun component6(): T6 = tail.tail.tail.tail.tail.head
    operator fun component7(): T7 = tail.tail.tail.tail.tail.tail.head
    operator fun component8(): T8 = tail.tail.tail.tail.tail.tail.tail.head
    operator fun component9(): T9 = tail.tail.tail.tail.tail.tail.tail.tail.head
    operator fun component10(): T10 = tail.tail.tail.tail.tail.tail.tail.tail.tail.head
    operator fun component11(): T11 = tail.tail.tail.tail.tail.tail.tail.tail.tail.tail.head
}
class HList12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>(val head: T1, val tail: HList11<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>) {
    operator fun component1(): T1 = head
    operator fun component2(): T2 = tail.head
    operator fun component3(): T3 = tail.tail.head
    operator fun component4(): T4 = tail.tail.tail.head
    operator fun component5(): T5 = tail.tail.tail.tail.head
    operator fun component6(): T6 = tail.tail.tail.tail.tail.head
    operator fun component7(): T7 = tail.tail.tail.tail.tail.tail.head
    operator fun component8(): T8 = tail.tail.tail.tail.tail.tail.tail.head
    operator fun component9(): T9 = tail.tail.tail.tail.tail.tail.tail.tail.head
    operator fun component10(): T10 = tail.tail.tail.tail.tail.tail.tail.tail.tail.head
    operator fun component11(): T11 = tail.tail.tail.tail.tail.tail.tail.tail.tail.tail.head
    operator fun component12(): T12 = tail.tail.tail.tail.tail.tail.tail.tail.tail.tail.tail.head
}

fun <T> hlist(t: T): HList1<T> = HList1(t)
fun <T1, T2> hlist(t1: T1, t2: T2): HList2<T1, T2> = hlist(t2).prepend(t1)
fun <T1, T2, T3> hlist(t1: T1, t2: T2, t3: T3): HList3<T1, T2, T3> = hlist(t2, t3).prepend(t1)
fun <T1, T2, T3, T4> hlist(t1: T1, t2: T2, t3: T3, t4: T4): HList4<T1, T2, T3, T4> = hlist(t2, t3, t4).prepend(t1)
fun <T1, T2, T3, T4, T5> hlist(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5): HList5<T1, T2, T3, T4, T5> = hlist(t2, t3, t4, t5).prepend(t1)
fun <T1, T2, T3, T4, T5, T6> hlist(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6): HList6<T1, T2, T3, T4, T5, T6> = hlist(t2, t3, t4, t5, t6).prepend(t1)
fun <T1, T2, T3, T4, T5, T6, T7> hlist(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7): HList7<T1, T2, T3, T4, T5, T6, T7> = hlist(t2, t3, t4, t5, t6, t7).prepend(t1)
fun <T1, T2, T3, T4, T5, T6, T7, T8> hlist(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8): HList8<T1, T2, T3, T4, T5, T6, T7, T8> = hlist(t2, t3, t4, t5, t6, t7, t8).prepend(t1)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> hlist(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9): HList9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = hlist(t2, t3, t4, t5, t6, t7, t8, t9).prepend(t1)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> hlist(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10): HList10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = hlist(t2, t3, t4, t5, t6, t7, t8, t9, t10).prepend(t1)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> hlist(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11): HList11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> = hlist(t2, t3, t4, t5, t6, t7, t8, t9, t10, t11).prepend(t1)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> hlist(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12): HList12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> = hlist(t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12).prepend(t1)
