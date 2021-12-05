import java.lang.ArithmeticException
import kotlin.math.abs
import kotlin.math.max
import kotlin.properties.Delegates
import kotlin.random.Random

class Tester {
    private lateinit var a: Array<Float>
    private lateinit var b: Array<Float>
    private lateinit var c: Array<Float>
    private lateinit var p: Array<Float>
    private lateinit var f: Array<Float>
    private lateinit var q: Array<Float>
    private lateinit var x: Array<Float>
    private var avgMark by Delegates.notNull<Float>()
    private lateinit var solvedX: Array<Float>

    /**
     * @param kolTests количество систем, которые надо решить
     * @param range диапазон значений
     * @param kolEquation количество уравнений
     */
    fun test(kolTests: Int, range: Float, kolEquation: Int): Pair<Float, Float> {
        val absRange = abs(range)
        val epsAvgs = arrayOfZeros(kolTests)
        val avgMarks = arrayOfZeros(kolTests)
        var currX = arrayOfZeros(kolEquation)
        var noErr = false

        for (i in 0 until kolTests) {
            avgMarks[i] = 0f
            while (!noErr) {
                generateArrays(kolEquation, absRange)
                currX = generateX(kolEquation, absRange)
                generateF(currX)
                noErr = solveSystem(
                    a,
                    b,
                    c,
                    p,
                    q,
                    f,
                ) == 0 //если все функции выдали нули, значит ошибки не было
                avgMarks[i] = this.avgMark
            }
            epsAvgs[i] = 0f
            for (j in 0 until kolEquation) {
                epsAvgs[i] = max(abs(currX[j] - solvedX[j]), epsAvgs[i])
            }
        }

        // epsAvg средняя относительная погрешность
        // avgMark среднее значение оценки точности
        var epsAvg = 0f
        var avgMark = 0f

        //а теперь считаем средние значения оценок
        for (i in 0 until kolTests) {
            epsAvg += epsAvgs[i]
            avgMark += avgMarks[i]
        }

        return epsAvg / kolTests to avgMark / kolTests
    }

    private fun generateArrays(kolEquation: Int, range: Float) {
        a = arrayOfZeros(kolEquation - 1)
        b = arrayOfZeros(kolEquation)
        c = arrayOfZeros(kolEquation - 1)
        p = arrayOfZeros(kolEquation)
        q = arrayOfZeros(kolEquation)
        x = arrayOfZeros(kolEquation)

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

    private fun generateX(kolEquation: Int, range: Float): Array<Float> {
        val x = arrayOfZeros(kolEquation)
        for (i in 0 until kolEquation - 1) {
            x[i] = Random.nextFloat() * 2 * range - range
        }
        return x
    }

    private fun generateX1(kolEquation: Int): Array<Float> {
        val x = arrayOfZeros(kolEquation)
        for (i in 0 until kolEquation) {
            x[i] = 1f
        }
        return x
    }

    private fun generateF(xx: Array<Float>) {
        val N = b.size
        f = arrayOfZeros(N)
        for (i in 0 until N) {
            f[0] += (p[i] * xx[i])
            f[1] += (q[i] * xx[i])
        }

        for (i in 2 until N) {
            f[i] = a[i - 1] * xx[i - 1] + b[i] * xx[i]
            if (i < N - 1)
                f[i] += (c[i] * xx[i + 1])
        }
    }

    private fun solveSystem(
        _a: Array<Float>,
        _b: Array<Float>,
        _c: Array<Float>,
        _p: Array<Float>,
        _q: Array<Float>,
        _f: Array<Float>
    ): Int {
        avgMark = 0f
        val N = x.size
        val bufa = _a
        val bufb = _b
        val bufc = _c
        val bufp = _p
        val bufq = _q

        a = _a.clone()
        b = _b.clone()
        c = _c.clone()
        p = _p.clone()
        q = _q.clone()
        f = _f.clone()

        val ans1 = findAnswer1stage()
        val ans2 = findAnswer2stage()
        val ans3 = findAnswer3stage()

        var result = ans1 + ans2 + ans3

        solvedX = x.clone()

        if (result > 0)
            return result

        a = bufa
        b = bufb
        c = bufc
        p = bufp
        q = bufq

        x = generateX1(b.size)
        generateF(x)

        result = findAnswer1stage() + findAnswer2stage() + findAnswer3stage()
        if (result > 0)
            return result

        for (i in 0 until N) {
            avgMark = max(avgMark, abs(x[i] - 1))
        }

        return 0
    }

    private fun findAnswer1stage(): Int {
        val N = b.size
        var chis: Float
        for (i in N - 1 downTo 2) {
            if (i < N - 1) {
                chis = -c[i]
                b[i] += (a[i] * chis)
                c[i] = 0f
                f[i] += (f[i + 1] * chis)
            }
            if (b[i] != 0f) {
                chis = 1 / b[i]
            } else
                return i + 1
            a[i - 1] *= (chis)
            b[i] = 1f
            f[i] *= (chis)

            chis = -p[i]
            p[i] = 0f
            p[i - 1] += (a[i - 1] * chis)
            f[0] += (f[i] * chis)

            chis = -q[i]
            q[i] = 0f
            q[i - 1] += (a[i - 1] * chis)
            f[1] += (f[i] * chis)

        }
        c[0] = p[1]
        b[1] = q[1]
        c[1] = q[2]
        return 0
    }

    private fun findAnswer2stage(): Int {
        var chis: Float
        if (b[1] != 0f)
            chis = -c[0] / b[1]
        else
            return 1
        b[0] += (a[0] * chis)
        c[0] = 0f
        f[0] += (f[1] * chis)

        if (b[0] != 0f) {
            chis = 1 / b[0]
        } else
            return 1
        b[0] = 1f
        f[0] *= chis


        chis = -a[0]
        a[0] = 0f
        f[1] += chis * f[0]

        if (b[1] != 0f) {
            chis = 1 / b[1]
        } else
            return 2
        b[1] = 1f
        f[1] *= chis

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
}

fun arrayOfZeros(size: Int): Array<Float> = Array(size, init = { 0f })