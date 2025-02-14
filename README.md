# ⚙️ modeltestutils: Utility Class for Reflection in Java

This utility class leverages Java's Reflection API to dynamically instantiate various classes and evaluate their constructors, getters, setters, `equals`, `hashCode`, and `clone` methods through JUnit5 and Mockito. It is designed to simplify and automate the testing and validation of object behaviors for different types.

---

## 🚀 Features

- **Dynamic Instantiation**: Create instances of different classes dynamically using reflection.
- **Constructor Evaluation**: Automatically checks and validates constructors for the target classes.
- **Getter and Setter Validation**: Ensures that getter and setter methods behave as expected.
- **Equality & Hash Code Validation**: Verifies the consistency of `equals` and `hashCode` methods.
- **Cloning Evaluation**: Checks the correctness of the `clone` method for different objects.

---

## 🛠️ Setup

To use the utility class in your project, you need to include it in your Java codebase.

---

## 🧰 Requirements

- **Java 8+**: The utility class is compatible with Java 8 and later versions.

---

## 📄 License

This utility class is open-source and available under the MIT License.

---

### 🚨 Note:
Make sure to handle exceptions that may arise during reflection, as some operations can throw `NoSuchMethodException`, `InvocationTargetException`, or `IllegalAccessException`.

---

📌 **Conclusion**  
This utility class makes it easier to test and validate the core functionality of your Java objects. By utilizing reflection, you can automate and streamline the evaluation of object behavior across different classes.

