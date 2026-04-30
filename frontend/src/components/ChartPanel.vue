<template>
  <div>
    <h2>可视化图表</h2>
    <div ref="chartRef" class="chart"></div>
  </div>
</template>

<script setup>
import * as echarts from 'echarts';
import { nextTick, onMounted, ref, watch } from 'vue';

const props = defineProps({
  option: {
    type: Object,
    default: () => ({})
  }
});

const chartRef = ref(null);
let chart;

function renderChart() {
  if (!chartRef.value) {
    return;
  }
  if (!chart) {
    chart = echarts.init(chartRef.value);
  }
  chart.setOption(props.option || {}, true);
}

onMounted(() => {
  nextTick(renderChart);
});

watch(() => props.option, () => nextTick(renderChart), { deep: true });
</script>
