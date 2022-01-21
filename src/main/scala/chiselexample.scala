package integration

import freechips.rocketchip.config._
import chisel3.stage._
import firrtl.options.TargetDirAnnotation
import firrtl.stage.{CompilerAnnotation, RunFirrtlTransformAnnotation}
import firrtl.transforms.NoDCEAnnotation
import firrtl.{EmitAllModulesAnnotation, VerilogCompiler, VerilogEmitter}
import freechips.rocketchip.diplomacy.{LazyModule, ValName}
import freechips.rocketchip.subsystem._
import freechips.rocketchip.system._
import chiselexamples._
import boom.common.WithNSmallBooms
object chiselexampletop extends App {
  (new ChiselStage).run(Seq(
    TargetDirAnnotation("./builds/chiselexamples"),
    ChiselGeneratorAnnotation(() => new MyModule),
    RunFirrtlTransformAnnotation(new VerilogEmitter)
  ))
}
object rocket extends App {
  val param = new TinyConfig
  (new chisel3.stage.ChiselStage).run(
    Seq(
      TargetDirAnnotation("./builds/rocket"),
      ChiselGeneratorAnnotation(() => new TestHarness()(param)),
      RunFirrtlTransformAnnotation(new VerilogEmitter)
    )
  )
}
object smallboom extends App {
  val param = new SmallBoomConfig
  (new chisel3.stage.ChiselStage).run(
    Seq(
      TargetDirAnnotation("./builds/rocket"),
      ChiselGeneratorAnnotation(() => new TestHarness()(param)),
      RunFirrtlTransformAnnotation(new VerilogEmitter)
    )
  )
}
class SmallBoomConfig extends Config(
  new WithNSmallBooms(1) ++
    new WithCoherentBusTopology ++
    new WithoutTLMonitors ++
    new BaseConfig().alter((site, here, up) => {
      case MemoryBusKey => up(MemoryBusKey).copy(beatBytes = 16)
      case SystemBusKey => up(SystemBusKey).copy(beatBytes = 16)
    })
)