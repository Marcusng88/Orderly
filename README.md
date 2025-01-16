# Orderly To-Do List

Orderly To-Do List is a sleek and user-friendly task management application designed to help you organize your tasks effectively. With features like task creation, categorization, and easy management, it's the perfect tool to stay on top of your to-do lists.

## Features
- Create, update, and delete tasks.(normal task,dependent task,recurring task)
- Sort tasks
- Vector search or semantic search
- Search and filter tasks for better accessibility.
- Persistent data storage using MySQL.
  

## Prerequisites
Before you begin, ensure you have the following installed on your machine:
- [Python](https://www.python.org/downloads/) (version 3.8 or later)
- [MySQL](https://dev.mysql.com/downloads/installer/)
- [pip](https://pip.pypa.io/en/stable/installation/) (Python package manager)

## Installation

Follow these steps to set up the project:

1. Clone the repository:
   ```bash
   git clone https://github.com/Marucsng88/Orderly.git
   cd Orderly
   ```

2. Create and activate a virtual environment (optional but recommended):
   ```bash
   python -m venv venv
   source venv/bin/activate  # For Linux/macOS
   venv\Scripts\activate    # For Windows
   ```

3. Install the required dependencies:
   ```bash
   pip install -r requirements.txt
   ```

4. Set up the MySQL database:
   - Create a MySQL database for the application, use the commands in todolist.sql to create the database in MySQL workbench
   - Update the database configuration in the project code (e.g., `Vector_search.java` and `Database.java` file) with your database credentials.
  

## Usage
1. Start the application:
   ```bash
   python embedding_server.py
   ```

2. Begin managing your tasks (Run the Orderly.java)!

## Development
If you want to contribute or make changes:

1. Fork the repository and create a new branch:
   ```bash
   git checkout -b feature-name
   ```

2. Make your changes and commit them:
   ```bash
   git add .
   git commit -m "Add feature description"
   ```

3. Push your changes and create a pull request:
   ```bash
   git push origin feature-name
   ```

## License
This project is licensed under the [MIT License](LICENSE).

## Contributions
Contributions are welcome! Feel free to open issues or submit pull requests.

## Contact
For any questions or support, please reach out to [ngzhengjie888@gmail.com](mailto:ngzhengjie888@gmail.com).

---
Happy organizing! 🎉
