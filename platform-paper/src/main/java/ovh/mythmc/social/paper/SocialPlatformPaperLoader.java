package ovh.mythmc.social.paper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class SocialPlatformPaperLoader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        String defaultMirror = "https://maven.aliyun.com/repository/central";
        try {
            Field field = MavenLibraryResolver.class.getDeclaredField("MAVEN_CENTRAL_DEFAULT_MIRROR");
            if (Modifier.isStatic(field.getModifiers())) {
                // Temporarily disabled since Google's mirror has issues at the time of release
                //defaultMirror = (String) field.get(null);
            }
        } catch (NoSuchFieldException /* | IllegalAccessException */ e) {
            // Ignore
        }

        resolver.addRepository(new RemoteRepository.Builder("central", "default", defaultMirror).build());
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-core:2.0.0"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-minecraft-extras:2.0.0-beta.15"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-paper:2.0.0-beta.15"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-brigadier:2.0.0-beta.15"), null));

        classpathBuilder.addLibrary(resolver);
    }

}
