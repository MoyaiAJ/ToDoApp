# ToDoApp

A simple To-Do list application built with Jetpack Compose. The app demonstrates essential Android concepts, including state management, state hoisting, data classes, recomposition, and persistence.

## Features

- Add tasks with an input field and button (empty input not allowed)
- Active and Completed sections
- Toggle tasks between Active and Completed with a checkbox
- Delete tasks from either section
- Friendly empty-state messages
- State persists across configuration changes using `rememberSaveable`

## Concepts Used

- Data class (`ToDoItem`)
- State management with `mutableStateListOf`, `remember`, and `rememberSaveable`
- State hoisting for stateless composables
- Compose layouts with `Column`, `Row`, `LazyColumn`, `TextField`, `Button`, `Checkbox`, `IconButton`
- Unidirectional data flow and recomposition
