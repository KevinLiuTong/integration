package integration
import boom.common.{BoomCoreParams, WithNSmallBooms}
import chisel3.{Bundle, Module}
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}
import firrtl.VerilogEmitter
import firrtl.options.TargetDirAnnotation
import firrtl.stage.RunFirrtlTransformAnnotation
import freechips.rocketchip.config._
import freechips.rocketchip.devices.tilelink.TLTestRAM
import freechips.rocketchip.diplomacy.{AddressSet, LazyModule, ValName}
import freechips.rocketchip.subsystem.{BaseSubsystem, BaseSubsystemModuleImp, InSubsystem, MemoryBusKey, SystemBusKey, TilesLocated, WithCoherentBusTopology, WithInclusiveCache}
import freechips.rocketchip.system.BaseConfig
import freechips.rocketchip.tile.{TileKey, TileVisibilityNodeKey}
import freechips.rocketchip.tilelink.{TLEphemeralNode, TLWidthWidget}
import vlsu._
import stone._
class VLSUBoomConfig extends Config(
  new WithNSmallBooms(1) ++
    new WithCoherentBusTopology ++
    new WithInclusiveCache++
    new BaseConfig().alter((site, here, up) => {
      case MemoryBusKey => up(MemoryBusKey).copy(beatBytes = 32, blockBytes = 64)
      case SystemBusKey => up(SystemBusKey).copy(beatBytes = 32, blockBytes = 64)
      case TileKey => site(TilesLocated(InSubsystem)).head.tileParams
      case TileVisibilityNodeKey => TLEphemeralNode()(ValName("master"))
    })
)

object vlsutop extends App {
  val param = new VLSUBoomConfig
  val gp = VLSUGeneralParameters()
  (new ChiselStage).run(
    Seq(
      TargetDirAnnotation("./builds/vlsu"),
      ChiselGeneratorAnnotation(() => new VLSUHarness(gp)(param)),
      RunFirrtlTransformAnnotation(new VerilogEmitter)
    )
  )
}

//./mill integration.runMain "integration.carocktop"
object stonetop extends App {
  val cp = CustomerParameters()
  val gp = GeneralParameters(cp)
  val ap = ArchParameters(gp)
  (new ChiselStage).run(
    Seq(
      TargetDirAnnotation("./builds/stone"),
      ChiselGeneratorAnnotation(() => new StoneBackEnd(ap)),
      RunFirrtlTransformAnnotation(new VerilogEmitter)
    )
  )
}


