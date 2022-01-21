package integration
import boom.common._
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

object chiselexampletop extends App {
  (new ChiselStage).run(Seq(
    TargetDirAnnotation("./builds/chiselexamples"),
    ChiselGeneratorAnnotation(() => new MyModule),
    RunFirrtlTransformAnnotation(new VerilogEmitter)
  ))
}
