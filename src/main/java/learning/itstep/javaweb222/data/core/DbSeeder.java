
package learning.itstep.javaweb222.data.core;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.core.seed.SeedActivity;
import learning.itstep.javaweb222.data.core.seed.SeedCareerEducation;
import learning.itstep.javaweb222.data.core.seed.SeedDemoChats;
import learning.itstep.javaweb222.data.core.seed.SeedDemoPosts;
import learning.itstep.javaweb222.data.core.seed.SeedDemoUsers;
import learning.itstep.javaweb222.data.core.seed.SeedJobs;
import learning.itstep.javaweb222.data.core.seed.SeedNetwork;
import learning.itstep.javaweb222.data.core.seed.SeedNotifications;
import learning.itstep.javaweb222.data.core.seed.SeedPortfolioDesigner;
import learning.itstep.javaweb222.data.core.seed.SeedProfileContent;
import learning.itstep.javaweb222.data.core.seed.SeedRolesUsers;

@Singleton
public class DbSeeder {

    private final SeedRolesUsers seedRolesUsers;
    private final SeedProfileContent seedProfileContent;
    private final SeedCareerEducation seedCareerEducation;
    private final SeedNetwork seedNetwork;
    private final SeedJobs seedJobs;
    private final SeedNotifications seedNotifications;
    private final SeedActivity seedActivity;
    private final SeedDemoPosts seedDemoPosts;
    private final SeedDemoUsers seedDemoUsers;
    private final SeedPortfolioDesigner seedPortfolioDesigner;
    private final SeedDemoChats seedDemoChats;

    @Inject
    public DbSeeder(
        SeedRolesUsers seedRolesUsers,
        SeedProfileContent seedProfileContent,
        SeedCareerEducation seedCareerEducation,
        SeedNetwork seedNetwork,
        SeedJobs seedJobs,
        SeedNotifications seedNotifications,
        SeedActivity seedActivity,
        SeedDemoPosts seedDemoPosts,
        SeedDemoUsers seedDemoUsers,
        SeedPortfolioDesigner seedPortfolioDesigner,
        SeedDemoChats seedDemoChats

    ) {
        this.seedRolesUsers = seedRolesUsers;
        this.seedProfileContent = seedProfileContent;
        this.seedCareerEducation = seedCareerEducation;
        this.seedNetwork = seedNetwork;
        this.seedJobs = seedJobs;
        this.seedNotifications = seedNotifications;
        this.seedActivity = seedActivity;
        this.seedDemoPosts = seedDemoPosts;
        this.seedDemoUsers = seedDemoUsers;
        this.seedPortfolioDesigner = seedPortfolioDesigner;
        this.seedDemoChats = seedDemoChats;

    }

    public boolean seed() {
        return
            seedRolesUsers.seed() &&
            seedProfileContent.seed() &&
            seedCareerEducation.seed() &&
            seedNetwork.seed() &&
            seedJobs.seed() &&
            seedNotifications.seed() &&
            seedActivity.seed() &&
            seedDemoPosts.seed()&&
            seedDemoUsers.seed()&&
            seedPortfolioDesigner.seed()&&
            seedDemoChats.seed();
        
    }
}
