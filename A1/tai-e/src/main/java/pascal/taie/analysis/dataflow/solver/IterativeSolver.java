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

package pascal.taie.analysis.dataflow.solver;

import pascal.taie.analysis.dataflow.analysis.DataflowAnalysis;
import pascal.taie.analysis.dataflow.fact.DataflowResult;
import pascal.taie.analysis.dataflow.fact.SetFact;
import pascal.taie.analysis.graph.cfg.CFG;
import pascal.taie.ir.exp.Var;

import java.util.ArrayList;
import java.util.Collections;

import java.util.HashSet;
import java.util.List;

class IterativeSolver<Node, Fact> extends Solver<Node, Fact> {

    public IterativeSolver(DataflowAnalysis<Node, Fact> analysis) {
        super(analysis);
    }

    @Override
    protected void doSolveForward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSolveBackward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        // TODO - finish me

        ArrayList<Node> listNode = new ArrayList<>();
        for(Node node : cfg){
            // 这里的node实际上就是ir上面的一个个顺序stmt
            listNode.add(node);
        }
        // 在ir上反过来就相当于反向的执行顺序
        Collections.reverse(listNode);
        boolean flag = true;

        ArrayList<Boolean> changelist = new ArrayList<>();

        //开始迭代算法实现
        while (flag){

            // 遍历每个节点
            for(Node node : listNode){

                // 寻找每个节点后继进行meet操作
                for(Node succ : cfg.getSuccsOf(node)){
                    analysis.meetInto(result.getInFact(succ), result.getOutFact(node)); //这行代码也是为什么要把node的in/out都实例的原因
                }
                // transfer 操作 ，返回是否变化，没变化就会返回false
                boolean tmp_flag = analysis.transferNode(node, result.getInFact(node), result.getOutFact(node) );
                changelist.add(tmp_flag);
            }

            HashSet<Boolean>changeset = new HashSet(changelist);
            if (changeset.size() == 1 && changeset.contains(false)){
                flag = false;
            }
            changelist.clear();

        }


    }
}