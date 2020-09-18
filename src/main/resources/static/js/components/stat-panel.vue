<template>
    <div>
        <div class="ch">Number of Users Online</div>
        <div class="center">
            {{count+1}}
        </div>
    </div>

</template>

<script>
module.exports = {
    data() {
        return {

        }
    },
    computed: {
        count() {
            return this.$store.state.count;
        }
    }
}
</script>

<style scoped>
.ch {
    font-size: 18px;
    font-weight: 600;
}
.center {
    text-align:center;
}
</style>