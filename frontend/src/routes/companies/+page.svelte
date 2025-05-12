<script>
    import axios from "axios";
    import { page } from "$app/state";
    import { onMount } from "svelte";
    import { jwt_token } from "../../store";

    // get the origin of current page, e.g. http://localhost:8080
    const api_root = page.url.origin;

    let currentPage = $state(1);
    let nrOfPages = $state(0);
    let defaultPageSize = $state(20);

    let companies = $state([]);
    let company = $state({
        name: null,
        email: null,
    });

    onMount(() => {
        getCompanies();
    });

    function changePage(pageNr) {
        currentPage = pageNr;
        getCompanies();
    }

    function getCompanies() {
        let query = `?pageSize=${defaultPageSize}&pageNumber=${currentPage}`;
        var config = {
            method: "get",
            url: api_root + "/api/company" + query, // Add query parameters
            headers: {
                Authorization: "Bearer " + $jwt_token,
            },
        };

        axios(config)
            .then(function (response) {
                companies = response.data.content; // Access content property
                nrOfPages = response.data.totalPages; // Get total pages
            })
            .catch(function (error) {
                alert("Could not get companies");
                console.log(error);
            });
    }

    function createCompany() {
        var config = {
            method: "post",
            url: api_root + "/api/company",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token,
            },
            data: company,
        };

        axios(config)
            .then(function (response) {
                alert("Company created");
                getCompanies();
            })
            .catch(function (error) {
                alert("Could not create Company");
                console.log(error);
            });
    }

    function validateEmailAndCreateCompany() {
        var config = {
            method: "get",
            url: "https://disify.com/api/email/" + company.email,
        };

        axios(config)
            .then(function (response) {
                console.log("Validated email " + company.email);
                console.log(response.data);
                if (
                    response.data.format &&
                    !response.data.disposable &&
                    response.data.dns
                ) {
                    createCompany();
                } else {
                    alert("Email " + company.email + " is not valid.");
                }
            })
            .catch(function (error) {
                alert("Could not validate email");
                console.log(error);
            });
    }
</script>

<h1 class="mt-3">Create Company</h1>
<form class="mb-5">
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="name">Name</label>
            <input
                bind:value={company.name}
                class="form-control"
                id="name"
                type="text"
            />
        </div>
    </div>
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="email">Email</label>
            <input
                bind:value={company.email}
                class="form-control"
                id="email"
                type="email"
            />
        </div>
    </div>
    <button
        type="button"
        class="btn btn-primary"
        onclick={validateEmailAndCreateCompany}>Submit</button
    >
</form>

<h1>All Companies</h1>
<table class="table">
    <thead>
        <tr>
            <th scope="col">Name</th>
            <th scope="col">Email</th>
            <th scope="col">ID</th>
        </tr>
    </thead>
    <tbody>
        {#each companies as company}
            <tr>
                <td>{company.name}</td>
                <td>{company.email}</td>
                <td>{company.id}</td>
            </tr>
        {/each}
    </tbody>
</table>

<nav aria-label="Company pagination">
    <ul class="pagination">
        {#each { length: nrOfPages } as _, i}
            <li class="page-item" class:active={currentPage === i + 1}>
                <button class="page-link" onclick={() => changePage(i + 1)}>
                    {i + 1}
                </button>
            </li>
        {/each}
    </ul>
</nav>

<style>
    .page-link:focus {
        box-shadow: none;
    }

    .page-item.active .page-link {
        z-index: 3;
        color: #fff;
        background-color: #0d6efd;
        border-color: #0d6efd;
    }
</style>
