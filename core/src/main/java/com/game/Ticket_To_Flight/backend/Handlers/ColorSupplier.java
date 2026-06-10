package com.game.Ticket_To_Flight.backend.Handlers;

import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class ColorSupplier {
    private static final List<Color> colorList = List.of(
        new Color(0.85f, 0.25f, 0.25f, 1.0f),     // Благородный красный (вместо ядовитого)
        new Color(0.88f, 0.45f, 0.15f, 1.0f),     // Мягкий мандариновый
        new Color(0.75f, 0.25f, 0.50f, 1.0f),     // Глубокая фуксия / Пурпурный
        new Color(0.20f, 0.60f, 0.70f, 1.0f),     // Спокойный морской волны
        new Color(0.92f, 0.72f, 0.15f, 1.0f),     // Теплый янтарный / Горчичный

        // Сложные и пастельные оттенки
        new Color(0.48f, 0.26f, 0.58f, 1.0f),     // Черничный / Аметистовый
        new Color(0.88f, 0.43f, 0.38f, 1.0f),     // Терракотовый / Приглушенный коралловый
        new Color(0.15f, 0.50f, 0.50f, 1.0f),     // Глубокий шалфейный (Teal)
        new Color(0.45f, 0.35f, 0.72f, 1.0f),     // Мягкий лавандово-синий
        new Color(0.18f, 0.46f, 0.25f, 1.0f),     // Хвойный зеленый
        new Color(0.80f, 0.62f, 0.18f, 1.0f),     // Старое золото
        new Color(0.35f, 0.52f, 0.66f, 1.0f),     // Джинсовый / Стальной синий
        new Color(0.68f, 0.42f, 0.70f, 1.0f),     // Матовая орхидея
        new Color(0.82f, 0.36f, 0.26f, 1.0f),     // Кирпично-томатный
        new Color(0.48f, 0.68f, 0.28f, 1.0f),     // Травяной зеленый
        new Color(0.42f, 0.56f, 0.82f, 1.0f),     // Приглушенный васильковый
        new Color(0.88f, 0.58f, 0.52f, 1.0f),     // Мягкий лососевый
        new Color(0.45f, 0.72f, 0.62f, 1.0f),     // Пыльная мята

        // Глубокие и сдержанные темные оттенки
        new Color(0.52f, 0.16f, 0.16f, 1.0f),     // Марсала / Винный
        new Color(0.16f, 0.26f, 0.46f, 1.0f),     // Полуночный синий
        new Color(0.24f, 0.34f, 0.34f, 1.0f),     // Матовый грифельный
        new Color(0.40f, 0.48f, 0.18f, 1.0f),     // Защитный оливковый
        new Color(0.58f, 0.32f, 0.20f, 1.0f),     // Обожженная сиена
        new Color(0.28f, 0.16f, 0.46f, 1.0f),     // Чернильный индиго
        new Color(0.58f, 0.68f, 0.56f, 1.0f),     // Блеклый зеленый чай
        new Color(0.50f, 0.30f, 0.18f, 1.0f),     // Кофейно-шоколадный
        new Color(0.82f, 0.70f, 0.54f, 1.0f),     // Крафтовый песочный
        new Color(0.32f, 0.66f, 0.48f, 1.0f)      // Хвойный весенний
    );

    private static int curColor = 0;

    public static Color getColor() {
        Color color = colorList.get(curColor);
        curColor = (curColor + 1) % colorList.size();
        return color;
    }

    public static void resetCurColor(){curColor = 0;}
}
