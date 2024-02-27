package app.revanced.patches.duolingo.unlocksuper.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstructions
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstructions
import app.revanced.patcher.fingerprint.MethodFingerprint
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.duolingo.unlocksuper.fingerprints.UserSerializationMethodFingerprint
import app.revanced.util.exception
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction22c
import com.android.tools.smali.dexlib2.iface.reference.Reference

private fun MethodFingerprint.resultOrThrow() = result ?: throw exception

@Patch(
    name = "Unlock Duolingo Super",
    description = "Unlocks Duolingo Super features.",
    compatiblePackages = [CompatiblePackage("com.duolingo")]
)
@Suppress("unused")
object UnlockDuolingoSuperPatch : BytecodePatch(
    setOf(
        UserSerializationMethodFingerprint,
    )
) {
    override fun execute(context: BytecodeContext) {
        UserSerializationMethodFingerprint.resultOrThrow().mutableMethod
            .apply {
                this.annotations.forEach(::println)
                val instr = this
                    .getInstructions()
                    .filterIsInstance<BuilderInstruction22c>()
                    .filter { it.opcode == Opcode.IPUT_BOOLEAN }
                    .first { it.reference.toString() == "Lcom/duolingo/user/m0;->C:Z" }

                val index = instr.location.index - 1
                this.replaceInstructions(index, "const/4 v2, 0x1")
            }
    }
}
