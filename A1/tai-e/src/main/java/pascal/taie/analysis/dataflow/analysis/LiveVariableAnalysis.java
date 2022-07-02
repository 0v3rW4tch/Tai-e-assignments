/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package pascal.taie.analysis.dataflow.analysis;

import pascal.taie.analysis.dataflow.fact.SetFact;
import pascal.taie.analysis.graph.cfg.CFG;
import pascal.taie.config.AnalysisConfig;
import pascal.taie.ir.exp.LValue;
import pascal.taie.ir.exp.RValue;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.stmt.Stmt;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of classic live variable analysis.
 */
public class LiveVariableAnalysis extends
        AbstractDataflowAnalysis<Stmt, SetFact<Var>> {

    public static final String ID = "livevar";

    public LiveVariableAnalysis(AnalysisConfig config) {
        super(config);
    }

    @Override
    public boolean isForward() {
        return false;
    }

    @Override
    public SetFact<Var> newBoundaryFact(CFG<Stmt> cfg) {
        // TODO - finish me
        // 因为这个分析Domain是Variable，边界初始化为范型为Var的SetFact
        // 因为这个函数默认返回Var类型的SetFact，所以<>内可以省略Var
        return new SetFact<>();

    }


    @Override
    public SetFact<Var> newInitialFact() {
        // TODO - finish me
        // 同样每个node都初始化为空的SetFact
        return new SetFact<>();

    }

    @Override
    public void meetInto(SetFact<Var> fact, SetFact<Var> target) {
        // TODO - finish me
        // meet操作是union
        target.union(fact);
    }

    @Override
    public boolean transferNode(Stmt stmt, SetFact<Var> in, SetFact<Var> out) {
        // TODO - finish me
        /*
        transfer function 实现
        1. 先从stmt里面获取相关gen 和 kill 相关内容
         */

        Optional<LValue> def = stmt.getDef(); //获取def
        List<RValue> uses = stmt.getUses(); //获取use

        SetFact<Var> useSetFact = new SetFact<>();

        for(Object obj : uses){
            if(obj instanceof Var){    //一定要保证变量类型
                useSetFact.add((Var) obj);   //放入useSetFact里面方便后面gen/kill操作调用
            }

        }

        //需要copy一份out里面的内容，不然会修改掉out，在下一轮循环中出错
        SetFact<Var> tmpOut = out.copy();

        //def 存在值且为var的时候就开始transfer的关键操作
        if(tmpOut.size() > 0 && def.isPresent() && def.get() != null && def.get() instanceof Var){
            // kill操作
            tmpOut.remove((Var) def.get());
        }

        //gen操作
        return in.union(tmpOut.unionWith(useSetFact));
    }
}