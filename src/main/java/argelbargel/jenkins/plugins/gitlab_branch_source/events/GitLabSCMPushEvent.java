package argelbargel.jenkins.plugins.gitlab_branch_source.events;


import argelbargel.jenkins.plugins.gitlab_branch_source.GitLabSCMNavigator;
import argelbargel.jenkins.plugins.gitlab_branch_source.GitLabSCMSource;
import argelbargel.jenkins.plugins.gitlab_branch_source.heads.GitLabSCMHead;
import com.dabsquared.gitlabjenkins.cause.CauseData;
import com.dabsquared.gitlabjenkins.gitlab.hook.model.PushHook;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;

import static argelbargel.jenkins.plugins.gitlab_branch_source.events.CauseDataHelper.buildCauseData;
import static argelbargel.jenkins.plugins.gitlab_branch_source.heads.GitLabSCMHead.createBranch;
import static java.util.Collections.singletonList;


public class GitLabSCMPushEvent extends GitLabSCMHeadEvent<PushHook> {
    public GitLabSCMPushEvent(String id, PushHook hook, String origin) {
        this(Type.UPDATED, id, hook, origin);
    }

    GitLabSCMPushEvent(Type type, String id, PushHook hook, String origin) {
        super(type, id, hook, origin);
    }

    @Nonnull
    @Override
    public final String getSourceName() {
        return getPayload().getProject().getPathWithNamespace();
    }

    @Override
    protected final boolean isMatch(@Nonnull GitLabSCMNavigator navigator) {
        return super.isMatch(navigator) && EventHelper.getMatchingProject(navigator, getPayload()) != null;
    }

    @Override
    protected boolean isMatch(@Nonnull GitLabSCMSource source) {
        return super.isMatch(source) && getPayload().getProjectId().equals(source.getProjectId()) && source.getSourceSettings().getBranchMonitorStrategy().getMonitored();
    }

    @Override
    Collection<? extends GitLabSCMHead> heads(@Nonnull GitLabSCMSource source) throws IOException, InterruptedException {
        PushHook hook = getPayload();
        return singletonList(createBranch(hook.getProjectId(), hook.getRef(), hook.getAfter()));
    }

    @Override
    final CauseData getCauseData() {
        return buildCauseData(getPayload());
    }
}
