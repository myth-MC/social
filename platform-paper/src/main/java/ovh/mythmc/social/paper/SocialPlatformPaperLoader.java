package ovh.mythmc.social.paper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

public final class SocialPlatformPaperLoader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        try {
            resolver.addRepository(new RemoteRepository.Builder("central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR).build());
        } catch (Exception e) {
            resolver.addRepository(new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/").build());
        }
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-core:2.0.0"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-minecraft-extras:2.0.0-beta.10"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-paper:2.0.0-beta.10"), null));

        classpathBuilder.addLibrary(resolver);
    }

}
