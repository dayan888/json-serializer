# json-serializer
Dynamically choose fields to include or exclude when you convert object to json string.

### description
Extend Object Mapper function when serialize object to json format string.

Usually when you serialize object to json string, if you want to choose which fields to include or exclude, you can achieve that by statically annotating fields of the object.

Using this library, you can dynamically specify fields to include or exclude by literal strings.Extend Object Mapper function when serialize object to json format string.

## build

./gradlew build

jar file is created at build/libs/json-0.3.jar

## How to use

See SerializerTest class.

For example, to serialise an object named targetObject, you can do by the following ways.

targetObject: an object to serialize.

includeList: String list of field name to include. You can use * to match anything and specify nested object by using "." as separator.

excludeList: String list of field name to exclude. You can use * to match anything and specify nested object by using "." as separator.

Estimation of which fields should be included is done in the following order.

1. If the field has JsonIgnore annotation or transient modifier then excluded.
2. If the field has public instance non-abstract get method, then use returned value of this getter.
3. If the object of the field has JsonInclude.Include.NON_NULL annotation and the value is null then excluded.
4. If the field is in the included list then included.
5. If the field is in the excluded list then excluded.
6. If none of above, isFinallyAllowed is true then included otherwise excluded.


### One liner

```aidl
Serializer.getInstance().toJsonString([targetObject], [includeList], [excludeList], [isFinallyAllowed]));
```

```aidl
Serializer.getInstance().toJsonString(targetObject, Arrays.asList(new String[]{"id", "url", "childSet.name"}), null, false));
```

### Using wrapper

```aidl
Wrapper wrapper = new Wrapper([targetObject]);
wrapper.addInclude([includeList or field path]);
wrapper.addExclude([excludeList or field path]);
wrapper.setFinallyAllowed([true or false]);

Serializer.getInstance().toJsonString(wrapper);

```


