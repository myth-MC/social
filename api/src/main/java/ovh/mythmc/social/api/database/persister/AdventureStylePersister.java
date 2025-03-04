package ovh.mythmc.social.api.database.persister;

import java.sql.SQLException;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public final class AdventureStylePersister extends StringType {
    
    private static final AdventureStylePersister INSTANCE = new AdventureStylePersister();

    private AdventureStylePersister() {
        super(SqlType.STRING, new Class<?>[] { Style.class });
    }

    public static AdventureStylePersister getSingleton() {
        return INSTANCE;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        return getStringFromStyle((Style) javaObject);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        return sqlArg != null ? getStyleFromString((String) sqlArg) : null;
    }

    private String getStringFromStyle(Style style) {
        final var dummyComponent = Component.empty()
            .style(style);

        return GsonComponentSerializer.gson().serialize(dummyComponent);
    }

    private Style getStyleFromString(String string) {
        return GsonComponentSerializer.gson().deserialize(string)
            .style();
    }

}
