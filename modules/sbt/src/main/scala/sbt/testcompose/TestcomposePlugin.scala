package sbt.testcompose

import sbt.*
import sbt.Keys.*

object TestcomposePlugin extends AutoPlugin {

  object autoImport extends TestcomposeKeys
  import autoImport.*

  override def globalSettings: Seq[Setting[?]] = Def.settings(
    disableTestcomposePrompt := true,
    testcomposePluginPrompt  := bootstrapPluginPrompt().value,
  )

  override def buildSettings: Seq[Def.Setting[_]] = Def.settings(
    dockerRepositoryEnv   := Option.empty,
    dockerImageVersionEnv := Option.empty,
    dockerComposeFileEnv  := Map.empty,
  )

  override def projectSettings: Seq[Setting[?]] = Def.settings(
    dockerComposeFile          := baseDirectory.value / "compose.yaml",
    dockerImageCreation        := nothingTask.value,
    Test / dockerImageCreation := dockerImageCreation.value,
    dockerComposeUp            := dockerComposeUpTask.value,
    dockerPs                   := dockerPsTask().value,
    dockerComposeRestart       := dockerComposeRestartTask.value,
    dockerComposeDown          := dockerComposeDownTask.value,
    dockerComposeTest          := dockerComposeTestTask.value,
    it                         := itTask.value,
    itOnly                     := itOnlyTask.evaluated,
    test                       := testTask.value,
    testOnly                   := testOnlyTask.evaluated,
  )
}
