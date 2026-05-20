package com.game.Ticket_To_Flight.backend;

import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class ColorSupplier {
    private static final List<Color> colorList = List.of(
// Базовые яркие
        new Color(1.0f, 0.0f, 0.0f, 1.0f),     // Красный
        new Color(0.0f, 0.0f, 1.0f, 1.0f),     // Синий
        new Color(0.0f, 1.0f, 0.0f, 1.0f),     // Зеленый
        new Color(1.0f, 0.5f, 0.0f, 1.0f),     // Оранжевый
        new Color(1.0f, 0.0f, 1.0f, 1.0f),     // Маджента
        new Color(0.0f, 1.0f, 1.0f, 1.0f),     // Циан / Голубой
        new Color(1.0f, 1.0f, 0.0f, 1.0f),     // Желтый

        // Насыщенные и пастельные оттенки
        new Color(0.5f, 0.0f, 0.5f, 1.0f),     // Пурпурный
        new Color(1.0f, 0.4f, 0.4f, 1.0f),     // Коралловый
        new Color(0.0f, 0.5f, 0.5f, 1.0f),     // Тёмно-бирюзовый (Teal)
        new Color(0.54f, 0.17f, 0.89f, 1.0f),  // Сине-фиолетовый
        new Color(0.13f, 0.55f, 0.13f, 1.0f),  // Лесной зеленый
        new Color(0.85f, 0.65f, 0.13f, 1.0f),  // Золотистый
        new Color(0.27f, 0.51f, 0.71f, 1.0f),  // Стальной синий
        new Color(0.73f, 0.33f, 0.83f, 1.0f),  // Орхидея
        new Color(1.0f, 0.39f, 0.28f, 1.0f),   // Томатный
        new Color(0.2f, 0.8f, 0.2f, 1.0f),     // Приятный лайм
        new Color(0.39f, 0.58f, 0.93f, 1.0f),  // Васильковый
        new Color(0.98f, 0.5f, 0.45f, 1.0f),   // Лососевый
        new Color(0.4f, 0.8f, 0.6f, 1.0f),     // Мятный

        // Более темные (для контраста)
        new Color(0.5f, 0.0f, 0.0f, 1.0f),     // Бордовый
        new Color(0.0f, 0.0f, 0.5f, 1.0f),     // Тёмно-синий
        new Color(0.18f, 0.31f, 0.31f, 1.0f),  // Темно-грифельный
        new Color(0.42f, 0.56f, 0.14f, 1.0f),  // Оливковый
        new Color(0.63f, 0.32f, 0.18f, 1.0f),  // Сиена
        new Color(0.29f, 0.0f, 0.51f, 1.0f),   // Индиго
        new Color(0.56f, 0.74f, 0.56f, 1.0f),  // Блекло-зеленый
        new Color(0.82f, 0.41f, 0.12f, 1.0f),  // Шоколадный
        new Color(0.96f, 0.64f, 0.38f, 1.0f),  // Песочный
        new Color(0.0f, 1.0f, 0.5f, 1.0f)      // Весенний зеленый
    );

    private static int curColor = 0;

    public static Color getColor() {
        Color color = colorList.get(curColor);
        curColor = (curColor + 1) % colorList.size();
        return color;
    }
}
