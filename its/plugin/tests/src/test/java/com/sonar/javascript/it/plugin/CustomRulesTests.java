/*
 * SonarSource :: JavaScript :: ITs :: Plugin :: Tests
 * Copyright (C) 2012 SonarSource
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.sonar.javascript.it.plugin;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.wsclient.issue.Issue;
import org.sonar.wsclient.issue.IssueClient;
import org.sonar.wsclient.issue.IssueQuery;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarRunner;

public class CustomRulesTests {

  @ClassRule
  public static Orchestrator orchestrator = Tests.ORCHESTRATOR;

  private static IssueClient issueClient;

  @BeforeClass
  public static void prepare() throws InterruptedException {
    orchestrator.resetData();
    SonarRunner build = Tests.createSonarRunnerBuild()
      .setProjectDir(TestUtils.projectDir("custom_rules"))
      .setProjectKey("custom-rules")
      .setProjectName("Custom Rules")
      .setProjectVersion("1.0")
      .setSourceDirs("src");
    orchestrator.getServer().provisionProject("custom-rules", "Custom Rules");
    orchestrator.getServer().associateProjectToQualityProfile("custom-rules", "js", "javascript-custom-rules-profile");
    orchestrator.executeBuild(build);

    issueClient = orchestrator.getServer().wsClient().issueClient();
  }

  @Test
  public void base_tree_visitor_check() {
    List<Issue> issues = issueClient.find(IssueQuery.create().rules("javascript-custom-rules:base")).list();

    assertThat(issues).hasSize(1);

    Issue issue = issues.get(0);
    assertThat(issue.line()).isEqualTo(2);
    assertThat(issue.message()).isEqualTo("Function expression.");
    assertThat(issue.debt()).isEqualTo("5min");
  }

  @Test
  public void subscription_base_visitor_check() {
    List<Issue> issues = issueClient.find(IssueQuery.create().rules("javascript-custom-rules:subscription")).list();

    assertThat(issues).hasSize(1);

    Issue issue = issues.get(0);
    assertThat(issue.line()).isEqualTo(11);
    assertThat(issue.message()).isEqualTo("For in statement.");
    assertThat(issue.debt()).isEqualTo("10min");
  }

}
