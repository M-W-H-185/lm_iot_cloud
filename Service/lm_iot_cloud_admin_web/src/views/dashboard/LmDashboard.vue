<template>
    <div>
        <el-row :gutter="30">
            <el-col :span="24">
                <div class="grid-content ep-bg-purple" >
                    <lm-hello></lm-hello>
                </div>
            </el-col>
        </el-row>

        <el-row :gutter="30" class="mt-[30px]">
            <el-col :span="24">
                <lm-order></lm-order>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>

import LmHello from '@/components/dashboard/LmHello.vue';
import LmOrder from '@/components/dashboard/LmOrder.vue';
import LmChartBar from '@/components/dashboard/LmChartBar.vue';

</script>

<style scoped>

</style>