package ovh.mythmc.social.api.database.persister;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import ovh.mythmc.social.api.util.Mutable;

import java.lang.reflect.Field;
import java.sql.SQLException;

/*
  Mutable.class is Serializable, this persister is used to enable compatibility with older database structures
 */
public final class MutableStringPersister extends StringType {

    private static final MutableStringPersister INSTANCE = new MutableStringPersister();

    private MutableStringPersister() {
        super(SqlType.STRING);
    }

    public static MutableStringPersister getSingleton() { return INSTANCE; }

    @Override
    public boolean isValidForField(Field field) {
        return Mutable.class.isAssignableFrom(field.getType()) &&
            field.getGenericType().getTypeName().contains("String");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        return getStringFromMutable((Mutable<String>) javaObject);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        return sqlArg != null ? getMutableFromString((String) sqlArg) : Mutable.empty();
    }

    private static String getStringFromMutable(Mutable<String> mutable) {
        return mutable.get();
    }

    private static Mutable<String> getMutableFromString(String string) {
        return Mutable.of(string);
    }
}
