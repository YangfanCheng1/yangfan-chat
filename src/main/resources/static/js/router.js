export const router = new VueRouter({
    routes: [
        {
            path: '/about-me',
            name: 'about',
            component: httpVueLoader('components/about.vue')
        }
    ]
})