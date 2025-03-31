<script>
  import axios from "axios";
  import { page } from "$app/state";
  import { onMount } from "svelte";
  import { jwt_token } from "../../store";

  // get the origin of current page, e.g. http://localhost:8080
  const api_root = page.url.origin;

  let companies = $state([]);
  let company = $state({
    name: null,
    email: null
  });

  onMount(() => {
    getCompanies();
  });

  function getCompanies() {
    var config = {
      method: "get",
      url: api_root + "/api/company",
      headers: {
        "Authorization": "Bearer " + $jwt_token
      },
    };

    axios(config)
      .then(function (response) {
        companies = response.data;
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
        "Authorization": "Bearer " + $jwt_token
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
  <button type="button" class="btn btn-primary" onclick={createCompany}
    >Submit</button
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

