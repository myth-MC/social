package ovh.mythmc.social.api.database.persister;

import java.lang.reflect.Field;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import ovh.mythmc.social.api.util.Mutable;

public final class AdventureMutableStylePersister extends StringType {
    
    private static final AdventureMutableStylePersister INSTANCE = new AdventureMutableStylePersister();

    private AdventureMutableStylePersister() {
        super(SqlType.STRING);
    }

    public static AdventureMutableStylePersister getSingleton() {
        return INSTANCE;
    }

    @Override
    public boolean isValidForField(Field field) {
        return Mutable.class.isAssignableFrom(field.getType()) &&
            field.getGenericType().getTypeName().contains("Style");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        return getStringFromMutableStyle((Mutable<Style>) javaObject);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return sqlArg != null ? getMutableStyleFromString((String) sqlArg) : Mutable.of(Style.empty());
    }

    private static String getStringFromMutableStyle(Mutable<Style> style) {
        final var dummyComponent = Component.empty()
            .style(style.get());

        return GsonComponentSerializer.gson().serialize(dummyComponent);
    }

    private static Mutable<Style> getMutableStyleFromString(String string) {
        return Mutable.of(GsonComponentSerializer.gson().deserialize(string)
            .style());
    }

}
