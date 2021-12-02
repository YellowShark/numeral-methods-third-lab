import java.lang.ArithmeticException
import kotlin.math.abs
import kotlin.math.max
import kotlin.properties.Delegates
import kotlin.random.Random

class Tester {
    private var a = mutableListOf<Float>()
    private var b = mutableListOf<Float>()
    private var c = mutableListOf<Float>()
    private var p = mutableListOf<Float>()
    private var f = mutableListOf<Float>()
    private var q = mutableListOf<Float>()
    private var x = mutableListOf<Float>()
    private var avgMark by Delegates.notNull<Float>()
    private lateinit var _x: List<Float>

    private fun generateArrays(kolEquation: Int, range: Float) {
        for (i in 0 until (kolEquation - 1)) {
            a[i] = Random.nextFloat() * 2 * range - range
            b[i] = Random.nextFloat() * 2 * range - range
            c[i] = Random.nextFloat() * 2 * range - range
        }

        b[kolEquation - 1] = Random.nextFloat() * 2 * range - range

        for (i in 3 until kolEquation) {
            p[i] = Random.nextFloat() * 2 * range - range
            q[i] = Random.nextFloat() * 2 * range - range
        }
        p[2] = Random.nextFloat() * 2 * range - range
        p[0] = b[0]
        p[1] = c[0]
        q[0] = a[0]
        q[1] = b[1]
        q[2] = c[1]
    }

    private fun generateX(kolEquation: Int, range: Float): List<Float> {
        val x = mutableListOf<Float>()
        for (i in 0 until kolEquation - 1) {
            x[i] = Random.nextFloat() * 2 * range - range
        }
        return x
    }

    private fun generateX1(kolEquation: Int): List<Float> {
        val x = mutableListOf<Float>()
        for (i in 0 until kolEquation) {
            x[i] = 1f
        }
        return x
    }

    private fun generateF(xx: List<Float>) {
        val N = b.size
        for (i in 0 until N) {
            f[0] += p[i] * xx[i]
            f[1] += q[i] * xx[i]
        }

        for (i in 2 until N) {
            f[i] = a[i - 1] * xx[i - 1] + b[i] * xx[i]
            if (i < N - 1)
                f[i] += c[i] * xx[i + 1]
        }
    }

    private fun findAnswer1stage(): Int {
        val N = b.size
        var chis: Float
        for (i in N - 1 downTo 1) {
            try {
                if (i < N - 1) {
                    chis = -c[i]
                    b[i] += a[i] * chis
                    c[i] = 0f
                    f[i] += f[i + 1] * chis
                }
                chis = 1 / b[i]
                a[i - 1] *= chis
                b[i] = 1f
                f[i] *= chis

                chis = -p[i]
                p[i] = 0f
                p[i - 1] += a[i - 1] * chis
                f[0] += f[i] * chis

                chis = -q[i]
                q[i] = 0f
                q[i - 1] += a[i - 1] * chis
                f[1] += f[i] * chis
            } catch (ex: ArithmeticException) {
                return i + 1
            }
        }
        c[0] = p[1]
        b[1] = q[1]
        c[1] = q[2]
        return 0
    }

    private fun findAnswer2stage(): Int {
        var chis: Float
        try {
            chis = -c[0] / b[1]
            b[0] += a[0] * chis
            c[0] = 0f
            f[0] += f[1] * chis

            chis = 1 / b[0]
            b[0] = 1f
            f[0] *= chis
        } catch (ex: ArithmeticException) {
            return 1
        }

        try {
            chis = -a[0]
            a[0] = 0f
            f[1] += chis * f[0]

            chis = 1 / b[1]
            b[1] = 1f
            f[1] *= chis
        } catch (ex: ArithmeticException) {
            return 2
        }
        return 0
    }

    private fun findAnswer3stage(): Int {
        val N = b.size
        for (i in 2 until N) {
            try {
                f[i] -= (a[i - 1] * f[i - 1])
                a[i - 1] = 0f
            } catch (ex: ArithmeticException) {
                return i + 1
            }
        }

        for (i in 0 until N) {
            x[i] = f[i]
        }
        return 0
    }

    private fun solveSystem(
        _a: List<Float>,
        _b: List<Float>,
        _c: List<Float>,
        _p: List<Float>,
        _q: List<Float>,
        _f: List<Float>
    ): Int {
        avgMark = 0f
        val N = x.size
        val bufa = _a
        val bufb = _b
        val bufc = _c
        val bufp = _p
        val bufq = _q

        a = _a.toMutableList()
        b = _b.toMutableList()
        c = _c.toMutableList()
        p = _p.toMutableList()
        q = _q.toMutableList()
        f = _f.toMutableList()

        var result = findAnswer1stage() + findAnswer2stage() + findAnswer3stage()

        _x = x

        if (result > 0)
            return result

        a = bufa.toMutableList()
        b = bufb.toMutableList()
        c = bufc.toMutableList()
        p = bufp.toMutableList()
        q = bufq.toMutableList()

        x = generateX1(b.size).toMutableList()
        generateF(x)

        result = findAnswer1stage() + findAnswer2stage() + findAnswer3stage()
        if (result > 0)
            return result

        for (i in 0 until N) {
            avgMark = max(avgMark, abs(x[i] - 1))
        }

        return 0
    }

    /// <summary />
    /// <param name="kolTests">количество систем, которые надо решить</param>
    /// <param name="range">диапазон значений</param>
    /// <param name="kolEquation">количество уравнений</param>
    /// <param name="epsAvg">средняя относительная погрешность</param>
    /// <param name="avgMark">среднее значение оценки точности</param>
    fun test(kolTests: Int, range: Float, kolEquation: Int): Pair<Float, Float> {
        var epsAvg = 0f
        var avgMark = 0f
        val _range = abs(range)
        val epsAvgs = mutableListOf<Float>()
        val avgMarks = mutableListOf<Float>()
        var curX = mutableListOf<Float>()
        val solvedX = mutableListOf<Float>()
        var noErr = false
        var curEps = 0

        for (i in 0 until kolTests) {
            avgMarks[i] = 0f
            while (!noErr) {
                generateArrays(kolEquation, _range)
                curX = generateX(kolEquation, _range).toMutableList()
                generateF(curX)
                noErr = solveSystem(
                    a,
                    b,
                    c,
                    p,
                    q,
                    f,
                ) == 0 //если все функции выдали нули, значит ошибки не было
            }
            epsAvgs[i] = 0f
            for (j in 0 until kolEquation) {
                epsAvgs[i] = max(abs(curX[j] - solvedX[j]), epsAvgs[i])
            }
        }

        //а теперь считаем средние значения оценок
        for (i in 0 until kolTests) {
            epsAvg += epsAvgs[i]
            avgMark += avgMarks[i]
        }
        epsAvg /= kolTests
        avgMark /= kolTests

        return epsAvg to avgMark
    }

}