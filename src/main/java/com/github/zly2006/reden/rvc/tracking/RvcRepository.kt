package com.github.zly2006.reden.rvc.tracking

import com.github.zly2006.reden.rvc.remote.IRemoteRepository
import net.minecraft.entity.player.PlayerEntity
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.InitCommand
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.exists

class RvcRepository(
    private val git: Git,
    val name: String = git.repository.workTree.name
) {
    fun commit(structure: TrackedStructure, message: String, committer: PlayerEntity?) {
        RvcFileIO.save(git.repository.workTree.toPath(), structure)
        git.add().addFilepattern("*.rvc").call()
        val cmd = git.commit()
        if (committer != null) {
            cmd.setAuthor(committer.nameForScoreboard, committer.uuid.toString() + "@mc-player.redenmc.com")
        }
        cmd.setMessage("$message\n\nUser-Agent: Reden-RVC")
        cmd.call()
    }

    fun push(remote: IRemoteRepository) {
        git.push()
            .setRemote(remote.gitUrl)
            .call()
    }

    fun fetch() {
        TODO() // Note: currently we have no gui for this
    }

    fun head() = checkout(RVC_BRANCH)

    fun checkout(tag: String) = TrackedStructure(name).apply {
        git.checkout().setName(tag).setForced(true).call()
        RvcFileIO.load(git.repository.workTree.toPath(), this)
    }

    companion object {
        val path = Path("rvc")
        const val RVC_BRANCH = "rvc"
        fun create(name: String) = RvcRepository(
            Git.init()
                .setDirectory(path / name)
                .setInitialBranch(RVC_BRANCH)
                .call()
        )

        fun clone(url: String): RvcRepository {
            var name = url.split("/").last().removeSuffix(".git")
            var i = 1
            while ((path / name).exists()) {
                name = "$name$i"
                i++
            }
            return RvcRepository(
                Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(path / name)
                    .call()
            )
        }
    }
}

private fun CloneCommand.setDirectory(path: Path) = setDirectory(path.toFile())
private fun InitCommand.setDirectory(path: Path) = setDirectory(path.toFile())