<template>
    <div>
        <navbar></navbar>
        <b-container fluid class="font-md">
            <!-- header/navbar -->
            <!-- main container -->
            <b-row>
                <b-col sm="2">
                    <div class="box-content">
                        <left-panel></left-panel>
                    </div>
                </b-col>
                <b-col sm="8">
                    <div class="box-content">
                        <right-panel></right-panel>
                    </div>
                </b-col>
                <b-col sm="2">
                    <div class="box-content">
                        <stat-panel></stat-panel>
                    </div>
                </b-col>
            </b-row>


        </b-container>
        <!-- footer -->
        <div>
            <div id="loader">
                <div id="bar"></div>
            </div>
        </div>
    </div>
</template>

<script>
module.exports = {
    data() {
        return {
            isLoading: false
        }
    },
    watch : {
        load(v1, v2) {
            let i = 0;
            if (i === 0) {
                i = 1;
                let elem = document.getElementById("bar");
                let width = 1;
                let id = setInterval(() => {
                    if (width >= 100) {
                        clearInterval(id);
                        // reset
                        elem.style.width = "0%";
                    } else {
                        width++;
                        elem.style.width = width + "%";
                    }
                }, 5);

            }
        }
    },
    computed: {
        load() {
            return this.$store.state.load;
        }
    }
}
</script>

<style scoped>
.font-md {
    font-family: 'Roboto', sans-serif;
    font-size: 18px;
}
.skinny {
    margin-left: 10px;
    margin-right: 10px;
}
#loader {
    height: 5px;
    position: fixed;
    bottom: 0;
    width: 100%;
}
#bar {
    width: 1%;
    height: 5px;
    background-color: #884aea;
}
</style>