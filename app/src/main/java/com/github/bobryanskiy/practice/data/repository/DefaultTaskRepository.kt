package com.github.bobryanskiy.practice.data.repository

import android.util.Log
import com.github.bobryanskiy.practice.data.local.dao.UsedTableDao
import com.github.bobryanskiy.practice.data.local.models.UsedTableEntity
import com.github.bobryanskiy.practice.data.remote.GigaChatApiService
import com.github.bobryanskiy.practice.data.remote.GigaChatMessage
import com.github.bobryanskiy.practice.data.remote.GigaChatTaskRequest
import com.github.bobryanskiy.practice.data.remote.TokenManager
import com.github.bobryanskiy.practice.data.remote.mapper.toDomainTask
import com.github.bobryanskiy.practice.domain.model.Task
import com.github.bobryanskiy.practice.domain.repository.TaskRepository
import javax.inject.Inject

class DefaultTaskRepository @Inject constructor(
    private val gigaChatApiService: GigaChatApiService,
    private val tokenManager: TokenManager,
    private val usedTableDao: UsedTableDao
) : TaskRepository {
    override suspend fun getNextTask(topic: String, difficulty: String): Task {
        var token: String? = null
        try {
            token = tokenManager.getValidToken()
        } catch (e: Exception) {
            Log.e("F", e.message.toString())
        }

        Log.d("F", "usedTables")
        val usedTables = usedTableDao.getUsedTables(topic, difficulty).take(6)

        val taskType = if ((0..1).random() == 0) "mcq" else "sql_constructor"

        val systemPrompt = """
            Ты — генератор учебных заданий по SQL для школьников. Твоя задача — создавать ровно одно задание за раз в строго заданном JSON-формате.
            
            ❗ ОБЯЗАТЕЛЬНЫЕ ПРАВИЛА:
            1. Отвечай ТОЛЬКО валидным JSON. Никакого текста до, после или внутри JSON (без пояснений, без ```json, без комментариев).
            2. Используй ТОЛЬКО один из двух форматов ниже — в зависимости от типа задания.
            3. Все строки — в двойных кавычках. НЕ используй символ " внутри значений. Если нужно — переформулируй без кавычек.
            4. Не добавляй, не удаляй и не переименовывай поля. Соблюдай регистр.
            5. Все тексты — на русском языке.
            
            📌 Формат 1: выбор одного варианта (mcq)
            {
              "type": "mcq",
              "question": "Чёткий вопрос без вариантов, без нумерации, без SQL-примеров",
              "options": ["Вариант A", "Вариант B", "Вариант C", "Вариант D"],
              "correct": "Точный текст одного из вариантов из options",
              "explanation": "Краткое объяснение на русском: почему этот вариант правильный?"
            }
            
            📌 Формат 2: конструктор SQL-запроса (sql_constructor)
            {
              "type": "sql_constructor",
              "question": "Чёткая инструкция: что должен делать SQL-запрос? Без примеров и вариантов.",
              "parts": ["SELECT", "*", "FROM", "students"],
              "shuffled_parts": ["students", "SELECT", "FROM", "*"],
              "explanation": "Краткое объяснение на русском: почему этот запрос правильный?"
            }
            
            ❗ Требования к mcq:
            - Поле "question" НЕ должно содержать варианты ответов, нумерацию (1., 2.) или SQL-код.
            - Поле "options" — массив из 3–4 строк. Каждая строка — это только SQL-запрос или фраза, без номеров и кавычек.
            - Поле "correct" — точная копия одного из элементов "options".
            
            ❗ Требования к sql_constructor:
            - "parts" — минимальные неделимые токены SQL-запроса в правильном порядке.
            - "shuffled_parts" — те же токены, перемешанные в случайном порядке.
            - Запрещено: пустые строки (""), объединённые токены ("SELECT *"), дублирование таблиц.
            - Пример правильного запроса "SELECT имя, фамилия FROM people":
                "parts": ["SELECT", "имя", ",", "фамилия", "FROM", "people"]
            
            ❗ Требование к разнообразию:
            - Используй разные таблицы: books, orders, employees, products, movies, cities, countries, users, cars, animals и т.д.
            - Используй разные столбцы: id, name, title, price, salary, birth_date, email, status и т.д.
            - Формулируй вопросы по-разному: "Составьте запрос...", "Какой запрос вернёт...", "Напишите SQL для...".
            - Избегай шаблонных фраз вроде "из таблицы students/people".
            """.trimIndent()

        val userMessage = """
            Сгенерируй УНИКАЛЬНОЕ задание уровня '$difficulty' типа '$taskType'.
            Тема: $topic.
            Запрещено использовать таблицы: ${usedTables.joinToString(", ")}.
            Используй новые формулировки и другие столбцы.
            Ответь ТОЛЬКО JSON.
            Если нарушишь формат — задание будет отклонено. Ответь строго по шаблону.
            """.trimIndent()

        val request = GigaChatTaskRequest(
            model = "GigaChat",
            messages = listOf(
                GigaChatMessage(
                    role = "system",
                    content = systemPrompt
                ),
                GigaChatMessage(
                    role = "user",
                    content = userMessage
                )
            ),
            temperature = 0.7f
        )

        if (token != null) {
            val response = gigaChatApiService.generateTask(
                bearerToken = "Bearer $token",
                request = request
            )
            if (response.isSuccessful) {
                val gigaResponse = response.body()
                val task = gigaResponse?.toDomainTask()
                val tableName = extractTableName(task!!)
                Log.d("F", tableName)
                if (tableName.isNotBlank()) {
                    usedTableDao.insert(UsedTableEntity(topic, difficulty, tableName))
                }
                return task
            } else {
                throw Exception("Ошибка API: ${response.code()}")
            }
        }
        throw Exception("Token initialization error")
    }
}

private fun extractTableName(task: Task): String {
    return when (task) {
        is Task.SqlConstructorTask -> {
            val parts = task.parts
            for (i in parts.indices) {
                if (parts[i].equals("FROM", ignoreCase = true) && i + 1 < parts.size) {
                    return parts[i + 1].trim()
                }
            }
            ""
        }

        is Task.MultipleChoiceTask -> {
            val text = task.description + " " + task.options.joinToString(" ")
            Regex("""FROM\s+([a-zA-Z_][a-zA-Z0-9_]*)""", RegexOption.IGNORE_CASE)
                .find(text)?.groupValues?.get(1) ?: ""
        }
    }
}
