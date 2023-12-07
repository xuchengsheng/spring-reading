## PropertyEditor

- [PropertyEditor](#propertyeditor)
  - [一、基本信息](#一基本信息)
  - [二、知识储备](#二知识储备)
  - [三、基本描述](#三基本描述)
  - [四、主要功能](#四主要功能)
  - [五、接口源码](#五接口源码)
  - [六、主要实现](#六主要实现)
  - [七、最佳实践](#七最佳实践)
  - [八、与其他组件的关系](#八与其他组件的关系)
  - [九、常见问题](#九常见问题)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、知识储备

1. **JavaBeans 规范**

   + 理解 JavaBeans 规范是关键，因为 `PropertyEditor` 是 JavaBeans 规范的一部分。了解 JavaBeans 的组件模型、属性、事件、方法等概念有助于更好地理解 `PropertyEditor` 的作用和应用场景。
2. **反射 API** 

   + 熟悉 Java 反射 API 会很有帮助，因为 `PropertyEditor` 经常与反射一起使用，特别是在动态地处理属性和调用 getter/setter 方法时。
3. **类型转换和数据绑定** 
   + 对类型转换和数据绑定的基本理解也是必要的，因为 `PropertyEditor` 常用于这些领域，尤其是在将字符串形式的配置值转换为复杂对象类型时。

### 三、基本描述

`PropertyEditor` 接口是 Java Beans 规范的一部分，主要用于处理 Java Bean 属性的转换和编辑。这个接口允许将属性值从一种格式转换为另一种（如从字符串到对象），并提供自定义编辑功能。

### 四、主要功能

1. **值转换** 

   + 允许将属性值从一种格式转换为另一种格式。最常见的是将字符串转换为相应的对象类型，例如，将日期的字符串表示转换为 `Date` 对象。

2. **文本编辑** 

   + 提供 `setAsText(String text)` 方法来接收一个字符串并将其转换为属性的类型。同样，`getAsText()` 方法用于将属性值转换为其字符串表示形式。

3. **状态管理** 

   + 通过 `setValue(Object value)` 和 `getValue()` 方法来设置和获取属性的当前值。

4. **自定义编辑器支持** 

   + 如果需要，可以提供一个自定义编辑器界面。`supportsCustomEditor()` 方法用于判断是否支持自定义编辑器，`getCustomEditor()` 方法用于获取自定义编辑器的实例。

5. **属性变更通知** 

   + 支持属性变更监听，允许通过 `addPropertyChangeListener(PropertyChangeListener listener)` 和 `removePropertyChangeListener(PropertyChangeListener listener)` 方法添加和移除属性变更监听器。

6. **Java 初始化字符串** 

   + `getJavaInitializationString()` 方法返回一个表示如何以代码形式初始化该属性值的字符串。

### 五、接口源码

`PropertyEditor` 接口在 Java Beans 规范中用于管理和编辑属性。包括方法来转换属性值（如 `setAsText` 和 `getAsText`）、获取和设置属性（`setValue` 和 `getValue`）、支持属性的图形展示（`isPaintable` 和 `paintValue`）以及生成属性的初始化代码（`getJavaInitializationString`）。此外，接口支持自定义编辑器的创建和使用，以及属性更改的监听机制。

```java
public interface PropertyEditor {

    /**
     * 设置要编辑的对象。基本类型如 "int" 必须包装为相应的对象类型，如 "java.lang.Integer"。
     * @param value 要编辑的新目标对象。注意，这个对象不应由 PropertyEditor 修改，而应创建一个新对象来保存任何修改后的值。
     */
    void setValue(Object value);

    /**
     * 获取属性值。
     * @return 属性的值。基本类型如 "int" 将被包装为相应的对象类型，如 "java.lang.Integer"。
     */
    Object getValue();

    /**
     * 判断此属性编辑器是否可进行绘制。
     * @return 如果类支持 paintValue 方法，则返回 true。
     */
    boolean isPaintable();

    /**
     * 在屏幕的给定区域内绘制值的表示。注意，PropertyEditor 负责做自己的剪切以适应给定的矩形。
     * 如果 PropertyEditor 不支持绘制（见 isPaintable），则此方法应是一个静默的无操作。
     * @param gfx 用于绘制的 Graphics 对象。
     * @param box Graphics 对象中我们应该绘制的矩形区域。
     */
    void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box);

    /**
     * 返回一段 Java 代码片段，可用于设置属性以匹配编辑器当前状态。此方法用于在通过属性编辑器进行更改时生成 Java 代码。
     * @return 代表当前值的 Java 代码的片段。它不应包含用于结束表达式的分号 (';')。
     */
    String getJavaInitializationString();

    /**
     * 以文本形式获取属性值。
     * @return 属性值作为可编辑的字符串。如果值无法表示为可编辑的字符串，则返回 null。
     * 如果返回非空值，则 PropertyEditor 应准备好将该字符串解析回 setAsText 中。
     */
    String getAsText();

    /**
     * 通过解析给定的字符串来设置属性值。如果字符串格式不正确或这种类型的属性不能表示为文本，则可能引发 IllegalArgumentException。
     * @param text 要解析的字符串。
     */
    void setAsText(String text) throws java.lang.IllegalArgumentException;

    /**
     * 如果属性值必须是一组已知的标记值之一，则此方法应返回这些标记的数组。这可以用来表示（例如）枚举值。
     * 如果 PropertyEditor 支持标记，则应支持使用 setAsText 中的标记值作为设置值的方式。
     * @return 此属性的标记值。如果此属性不能表示为标记值，则可能为 null。
     */
    String[] getTags();

    /**
     * PropertyEditor 可以选择提供一个完整的自定义组件，用于编辑其属性值。PropertyEditor 负责将自己连接到其编辑器组件，并通过触发 PropertyChange 事件来报告属性值更改。
     * @return 一个 java.awt.Component，允许用户直接编辑当前属性值。如果不支持此功能，则可能为 null。
     */
    java.awt.Component getCustomEditor();

    /**
     * 判断此属性编辑器是否支持自定义编辑器。
     * @return 如果 propertyEditor 可以提供自定义编辑器，则返回 true。
     */
    boolean supportsCustomEditor();

    /**
     * 添加一个值更改的监听器。当属性编辑器更改其值时，应在所有注册的 PropertyChangeListener 上触发 PropertyChangeEvent，指定属性名称为 null 并将自己作为源。
     * @param listener 要添加的 PropertyChangeListener。
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * 删除值更改的监听器。
     * @param listener 要移除的 PropertyChangeListener。
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

}
```

### 六、主要实现

1. **`ByteArrayPropertyEditor`**

   - 将字符串转换为字节数组，通常用于处理编码的数据如 BASE64。

2. **`CharacterEditor`**

   - 将字符串转换为单个字符。

3. **`CharArrayPropertyEditor`**

   - 将字符串转换为字符数组。

4. **`CharsetEditor`**

   - 用于将字符串（代表字符集名称）转换为 `java.nio.charset.Charset` 对象。

5. **`ClassArrayEditor`**

   - 将字符串数组转换为 `Class` 对象数组，每个字符串代表一个类名。

6. **`ClassEditor`**

   - 将单个字符串（类名）转换为 `Class` 类型的对象。

7. **`CurrencyEditor`**

   - 用于将字符串转换为 `java.util.Currency` 实例。

8. **`CustomBooleanEditor`**

   - 将字符串转换为布尔值，可以处理更多的布尔表示，如 "on"/"off"。

9. **`CustomCollectionEditor`**

   - 用于将字符串转换为特定类型的集合，如 `List` 或 `Set`。

10. **`CustomDateEditor`**

    - 将字符串转换为 `java.util.Date` 对象，通常需要一个日期格式。

11. **`CustomMapEditor`**

    - 将字符串转换为 `Map` 类型的对象，用于处理键值对格式的数据。

12. **`CustomNumberEditor`**

    - 将字符串转换为数字，包括各种数值类型如 `Integer`, `Double`, `BigDecimal` 等。

13. **`FileEditor`**

    - 将文件路径字符串转换为 `java.io.File` 对象。

14. **`InputSourceEditor`**

    - 用于将字符串转换为 `org.xml.sax.InputSource` 对象，通常用于处理 XML 数据源。

15. **`InputStreamEditor`**

    - 将资源位置字符串转换为 `java.io.InputStream`。

16. **`LocaleEditor`**

    - 将字符串转换为 `java.util.Locale` 对象，例如 "en_US"。

17. **`PathEditor`**

    - 类似于 `FileEditor`，但用于转换为 `java.nio.file.Path` 对象。

18. **`PatternEditor`**

    - 将字符串转换为 `java.util.regex.Pattern` 对象，用于正则表达式。

19. **`PropertiesEditor`**

    - 将字符串转换为 `java.util.Properties` 对象，用于处理属性文件格式的字符串。

20. **`ReaderEditor`**

    - 将资源位置字符串转换为 `java.io.Reader`。

21. **`ResourceBundleEditor`**

    - 将字符串转换为 `java.util.ResourceBundle`，用于国际化资源。

22. **`StringArrayPropertyEditor`**

    - 将逗号分隔的字符串转换为字符串数组。

23. **`StringTrimmerEditor`**

    - 去除字符串前后的空白，并可选地将空字符串转换为 `null`。

24. **`TimeZoneEditor`**

    - 将字符串转换为 `java.util.TimeZone` 对象。

25. **`URIEditor`**

    - 将字符串转换为 `java.net.URI` 对象。

26. **`URLEditor`**

    - 将字符串转换为 `java.net.URL` 对象。

27. **`UUIDEditor`**

    - 将字符串转换为 `java.util.UUID` 对象。

28. **`ZoneIdEditor`**

    - 将字符串转换为 `java.time.ZoneId` 对象，用于处理时区信息。

### 七、最佳实践

使用 `BeanWrapperImpl` 包装了 `MyBean` 类，并为 `Date` 类型属性注册了自定义编辑器 `MyCustomDateEditor` 来转换日期字符串。然后，为 `MyBean` 的 "date" 和 "path" 属性设置了值，并输出了经过处理的 `MyBean` 实例，展示了 Spring 中属性编辑器的应用和属性值的动态转换。

```java
public class PropertyEditorDemo {

    public static void main(String[] args) {
        // 创建一个 BeanWrapperImpl 实例，用于操作 MyBean 类。
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(MyBean.class);

        // 为 Date 类型的属性注册自定义的属性编辑器 MyCustomDateEditor。
        beanWrapper.overrideDefaultEditor(Date.class, new MyCustomDateEditor());

        // 设置 MyBean 类中名为 "date" 的属性值，使用字符串 "2023-12-5"。
        // 这里会使用注册的 MyCustomDateEditor 来将字符串转换为 Date 对象。
        beanWrapper.setPropertyValue("date", "2023-12-5");

        // 设置 MyBean 类中名为 "path" 的属性值，使用字符串 "/opt/spring-reading"。
        // 因为没有为 Path 类型注册特定的编辑器，所以使用默认转换逻辑。
        beanWrapper.setPropertyValue("path", "/opt/spring-reading");

        // 输出最终包装的 MyBean 实例。
        System.out.println("MyBean = " + beanWrapper.getWrappedInstance());
    }
}
```

`MyCustomDateEditor` 类是一个继承自 `PropertyEditorSupport` 的自定义属性编辑器，专门用于将符合 "`yyyy-MM-DD`" 格式的字符串转换为 `Date` 对象，以及将 `Date` 对象格式化为该字符串格式。这个类主要用于处理字符串与日期类型之间的相互转换，适用于 Spring 框架中的数据绑定和类型转换场景。

```java
public class MyCustomDateEditor extends PropertyEditorSupport {

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD");

    @Override
    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        try {
            setValue(this.dateFormat.parse(text));
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
        }
    }

    @Override
    public String getAsText() {
        Date value = (Date) getValue();
        return (value != null ? this.dateFormat.format(value) : "");
    }
}
```

`MyBean`是一个 Java Bean，提供了基本的属性封装和访问方法。通过 getter 和 setter 方法，可以方便地访问和修改这两个属性的值。此外， `toString` 方法提供了一种方便的方式来查看 `MyBean` 实例的当前状态。

```java
public class MyBean {

    private Path path;

    private Date date;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "path=" + path +
                ", date=" + date +
                '}';
    }
}
```

运行结果发现，`PropertyEditor` 接口的实现是类型转换过程的核心，使得可以在设置 Java Bean 属性时，自动将字符串转换为需要的复杂类型，如日期类型。这种转换是在 Spring 的上下文中处理配置和数据绑定时非常常见的场景。

```java
MyBean = MyBean{path=\opt\spring-reading, date=Thu Jan 05 00:00:00 CST 2023}
```

### 八、与其他组件的关系

1. **Java Beans**

   - `PropertyEditor` 是 Java Beans 规范的一部分，用于管理 Java Bean 属性的编辑和转换。允许 Bean 属性从一种类型转换为另一种类型，特别是从字符串到复杂类型的转换。

2. **Spring 框架**

   - 在 Spring 框架中，`PropertyEditor` 用于在运行时处理属性文件或注解中的数据转换。使得可以将配置文件（如 XML）中的字符串转换为 Java 对象的属性值。`PropertyEditor` 与 Spring 的 `BeanWrapper` 和 `DataBinder` 组件紧密相关，这些组件用于绑定和验证 Bean 属性。

3. **数据绑定**

   - `PropertyEditor` 在数据绑定过程中起着关键作用，尤其是在 Web 应用程序中。在将请求参数映射到对象属性时，用于将字符串参数转换为相应的属性类型。

4. **类型转换**

   - 尽管 Spring 3.0 之后推荐使用 `ConversionService` 作为主要的类型转换机制，`PropertyEditor` 仍然在旧代码和一些特定情况下被使用。`ConversionService` 提供了一种更一致和通用的类型转换方法，但 `PropertyEditor` 仍然在处理某些类型的属性编辑和转换方面发挥作用。

### 九、常见问题

1. **线程安全问题**

   - `PropertyEditor` 实例通常不是线程安全的。在多线程环境中共享 `PropertyEditor` 实例可能会导致数据不一致或竞争条件。

2. **状态管理**

   - `PropertyEditor` 保存状态信息，这意味着同一个实例不能同时用于多个属性或多次操作。每次使用时都需要创建新的实例或重置状态，这可能导致效率问题。

3. **复杂类型转换限制**

   - 对于一些复杂的数据类型，使用 `PropertyEditor` 进行转换可能会比较困难，特别是当类型转换涉及复杂的逻辑或多步骤处理时。

4. **自定义编辑器的开发和维护**

   - 我们自定义的 `PropertyEditor` 需要深入理解目标类型的内部结构和行为，这可能增加开发和维护的复杂性。

5. **与 Spring 的 `ConversionService` 的集成**

   - 自 Spring 3.0 引入 `ConversionService` 后，需要在 `PropertyEditor` 和 `ConversionService` 之间做出选择，或者在有些情况下需要它们共存，这可能导致配置和使用上的混淆。