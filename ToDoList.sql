USE ToDoList;

CREATE TABLE tasks (
id INT AUTO_INCREMENT PRIMARY KEY,
title VARCHAR(255) NOT NULL, 
description TEXT, 
status ENUM ('Incomplete', 'Complete') DEFAULT 'Incomplete',
due_date DATE,
category VARCHAR(50),
priority ENUM ('Low', 'Moderate', 'High'),
recurrence_interval ENUM ('Daily', 'Weekly', 'Monthly') DEFAULT NULL
);

CREATE TABLE task_dependencies (
id INT AUTO_INCREMENT PRIMARY KEY, 
task_id INT NOT NULL ,
dependency_id INT NOT NULL, 
FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE, 
FOREIGN KEY (dependency_id) REFERENCES tasks(id) ON DELETE CASCADE,
UNIQUE (task_id, dependency_id)
);

INSERT INTO tasks (title, description, due_date, category, priority)
VALUES ('Complete research', 'Research for project', '2024-10-12', 'Homework', 'High');

SELECT * FROM todolist.tasks;

INSERT INTO tasks (title, description, due_date, category, priority)
VALUES ('FOM', 'Comic Project', '2025-01-06', 'Group Project', 'High');

INSERT INTO tasks (title, description, due_date, category, priority)
VALUES ('CSO', 'Group Assignment', '2025-01-07', 'Group Project', 'High');

INSERT INTO tasks (title, description, due_date, category, priority)
VALUES ('ENGLISH', 'Test 2', '2025-01-02', 'Individual', 'High');

SELECT * FROM todolist.tasks;

INSERT INTO tasks (title, description, due_date, category, priority)
VALUES ('FOP', 'to-do-list', '2025-01-03', 'Group Project', 'High');

SELECT * FROM todolist.tasks;

ALTER TABLE tasks ADD COLUMN dependencies TEXT DEFAULT NULL;

SELECT * FROM todolist.tasks;

DELETE FROM tasks WHERE id='6';

